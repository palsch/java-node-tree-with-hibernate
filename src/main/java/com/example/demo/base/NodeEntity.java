package com.example.demo.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Getter
//@Setter

@NamedEntityGraph(
        name = "Node.childNodes",
        attributeNodes = @NamedAttributeNode("childNodes")
)
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "dtype")
//@JsonDeserialize(using = NodeDeserializer.class)

@Entity(name = "node")
@Table(indexes = {
        @Index(name = "idx_node_parent_id", columnList = "parent_id"),
        @Index(name = "idx_node_dtype", columnList = "dtype")
})
@Inheritance(strategy = InheritanceType.JOINED)
// Change of a class name breaks the code - Solutions could be to set the name manually on each class: @DiscriminatorValue("ALE_ANTRAG")
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)  // This tells Hibernate to create the dtype column
@DiscriminatorValue("node") // This tells Hibernate to set the value of the dtype column to "node" for this class
public abstract class NodeEntity<TChildNode extends NodeEntity<?>> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(insertable = false, updatable = false)
    private String dtype;

    // Reference to the parent node, if applicable
    @Getter(AccessLevel.NONE)

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // Foreign key to the parent node
    protected NodeEntity<?> parent;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = NodeEntity.class)
    @BatchSize(size = 10) // optimize the batch size for the child nodes -> reduce the number of queries
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("createdAt")
    private List<TChildNode> childNodes = new ArrayList<>();

    @JsonIgnore
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonIgnore
    @Transient
    private boolean deserialized = false;

    public NodeEntity() {
//        updateTypeFromDiscriminator();
    }

    @PrePersist
    private void updateTypeFromDiscriminator() {
        DiscriminatorValue discriminatorValue = this.getClass().getAnnotation(DiscriminatorValue.class);
        if (discriminatorValue != null) {
            this.dtype = discriminatorValue.value();
        } else {
            throw new IllegalArgumentException("DiscriminatorValue annotation is missing");
        }
    }

    /**
     * Update the nodeEntity itself with the given nodeEntity data (not the child nodes)
     *
     * @param nodeEntity the nodeEntity to update
     * @return a change log
     */
    protected abstract String updateNode(NodeEntity<?> nodeEntity);

    /**
     * Create a new child node.
     * <p>
     * If no child node is allowed, return an empty Optional
     *
     * @return the new child node or an empty Optional
     */
    public abstract Optional<TChildNode> createNewChildNode();

    public Optional<TChildNode> addNewChildNode() {
        Optional<TChildNode> newChildNodeOptional = createNewChildNode();

        if (newChildNodeOptional.isEmpty()) {
            return Optional.empty();
        }

        addNode(newChildNodeOptional.get());
        return newChildNodeOptional;
    }

    /**
     * This method is called after the node is added to the parent node
     * and before the node is returned to the client
     * Override this method to setup the node if needed
     * <p>
     * For example to setup child nodes, document uploads, etc.
     */
    protected void initializeNode() {
        // override this method to setup the node if needed
    }

    /**
     * Override this method if you want to prevent the last child node from being removed on special conditions
     *
     * @return true if the last child node can be removed
     */
    protected boolean isRemoveLastChildNodeAllowed() {
        return true;
    }

    /**
     * Override this method if you want to allow the removal of child nodes
     *
     * @return true if child nodes can be removed
     */
    protected boolean isRemoveChildNodesAllowed() {
        return false;
    }

    public String update(NodeEntity<?> nodeEntity) {
        log.info(this.updateNode(nodeEntity));
        return updateChildNodes(nodeEntity);
    }

    /**
     * Update the child nodes of the node
     * <p>
     * It won't add or remove child nodes
     *
     * @param updateNodeEntity
     */
    public String updateChildNodes(NodeEntity<?> updateNodeEntity) {
        List<TChildNode> newChildNodes = (List<TChildNode>) updateNodeEntity.getChildNodes();
        List<TChildNode> existingChildNodes = this.getChildNodes();

        // update existing child nodes
        newChildNodes.forEach(newChildNode -> {
            existingChildNodes.stream()
                    .filter(existingChildNode -> existingChildNode.getId().equals(newChildNode.getId()))
                    .findFirst()
                    .ifPresent(existingChildNode -> existingChildNode.update(newChildNode));
        });

        return "updated children";
    }

    /**
     * Find a nodeEntity by id recursively
     *
     * @param nodeEntity
     * @return the nodeEntity if found, otherwise empty Optional
     */
    public Optional<NodeEntity<?>> findNode(NodeEntity<?> nodeEntity) {
        // find the nodeEntity by id recursively
        if (nodeEntity == null || nodeEntity.getId() == null) {
            return Optional.empty();
        }

        return findNodeById(nodeEntity.getId());
    }

    /**
     * Find a node by id recursively
     */
    public Optional<NodeEntity<?>> findNodeById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }

        // is this the node?
        if (this.getId().equals(id)) {
            return Optional.of(this);
        }

        // find the node in the child nodes
        for (NodeEntity<?> childNodeEntity : childNodes) {
            Optional<NodeEntity<?>> foundNode = childNodeEntity.findNodeById(id);
            if (foundNode.isPresent()) {
                return foundNode;
            }
        }

        return Optional.empty();
    }

    public void addNode(TChildNode node) {
        node.parent = this;
        childNodes.add(node);
        node.initializeNode();
    }

    /**
     * Remove a node by id recursively
     *
     * @param id the id of the node to remove
     * @return true if the node was removed, otherwise false
     */
    public boolean removeNodeById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        // is this the node? -> do nothing (cant remove myself)
        if (this.getId().equals(id)) {
            return false;
        }

        // search for the node in the child nodes
        Optional<TChildNode> optionalFoundNode = childNodes.stream().filter(childNode -> childNode.getId().equals(id)).findFirst();
        if (optionalFoundNode.isPresent()) {
            TChildNode foundNode = optionalFoundNode.get();
            removeNode(foundNode);
            return true;
        }

        // search for the node in the child nodes recursively
        for (TChildNode childNode : childNodes) {
            if (childNode.removeNodeById(id)) {
                return true;
            }
        }

        return false;
    }

    public void removeNode(NodeEntity<?> nodeEntity) {
        // first try to remove from the current nodeEntity
        if (childNodes.contains(nodeEntity)) {
            assertRemoveChildNodesIsAllowed();

            if (childNodes.size() == 1) {
                assertRemoveLastChildNodeIsAllowed();
            }

            nodeEntity.parent = null;
            childNodes.remove(nodeEntity);
            return;
        }

        // if not found, try to remove from the child nodes
        for (TChildNode childNode : childNodes) {
            childNode.removeNode(nodeEntity);
        }
    }

    public void removeNodes() {
        assertRemoveChildNodesIsAllowed();
        assertRemoveLastChildNodeIsAllowed();

        for (TChildNode node : childNodes) {
            node.parent = null;
        }
        childNodes.clear();
    }

    private void assertRemoveChildNodesIsAllowed() {
        if (!isRemoveChildNodesAllowed()) {
            throw new IllegalArgumentException("Cannot remove child nodes");
        }
    }

    private void assertRemoveLastChildNodeIsAllowed() {
        if (!isRemoveLastChildNodeAllowed()) {
            throw new IllegalArgumentException("Cannot remove the last child node");
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        NodeEntity<?> nodeEntity = (NodeEntity<?>) o;
        return getId() != null && Objects.equals(getId(), nodeEntity.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
