package com.example.demo.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.BatchSize;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Getter
@Setter

@NamedEntityGraph(
        name = "Node.childNodes",
        attributeNodes = @NamedAttributeNode("childNodes")
)
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "dtype")
//@JsonDeserialize(using = NodeDeserializer.class)

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
// Change of a class name breaks the code - Solutions could be to set the name manually on each class: @DiscriminatorValue("ALE_ANTRAG")
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)  // This tells Hibernate to create the dtype column
public abstract class Node<TChildNode extends Node<?>> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Reference to the parent node, if applicable
    @Getter(AccessLevel.NONE)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // Foreign key to the parent node
    private Node<?> parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, targetEntity = Node.class)
    @BatchSize(size = 16) // optimize the batch size for the child nodes -> reduce the number of queries
    private List<TChildNode> childNodes = new ArrayList<>();

    @Column(insertable = false, updatable = false)
    private String dtype;

    @JsonIgnore
    @Transient
    private boolean deserialized = false;

    public Node() {
        updateTypeFromDiscriminator();
    }

    private void updateTypeFromDiscriminator() {
        DiscriminatorValue discriminatorValue = this.getClass().getAnnotation(DiscriminatorValue.class);
        if (discriminatorValue != null) {
            this.setDtype(discriminatorValue.value());
        } else {
            throw new IllegalArgumentException("DiscriminatorValue annotation is missing");
        }
    }

    /**
     * Update the node itself with the given node data (not the child nodes)
     *
     * @param node the node to update
     * @return a change log
     */
    protected abstract String updateNode(Node<?> node);

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

    public String update(Node<?> node) {
        log.info(this.updateNode(node));
        return updateChildNodes(node);
    }

    /**
     * Update the child nodes of the node
     * <p>
     * It won't add or remove child nodes
     *
     * @param updateNode
     */
    public String updateChildNodes(Node<?> updateNode) {
        List<TChildNode> newChildNodes = (List<TChildNode>) updateNode.getChildNodes();
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
     * Find a node by id recursively
     *
     * @param node
     * @return the node if found, otherwise empty Optional
     */
    public Optional<Node<?>> findNode(Node<?> node) {
        // find the node by id recursively
        if (node == null || node.getId() == null) {
            return Optional.empty();
        }

        return findNodeById(node.getId());
    }

    /**
     * Find a node by id recursively
     */
    public Optional<Node<?>> findNodeById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }

        // is this the node?
        if (this.getId().equals(id)) {
            return Optional.of(this);
        }

        // find the node in the child nodes
        for (Node<?> childNode : childNodes) {
            Optional<Node<?>> foundNode = childNode.findNodeById(id);
            if (foundNode.isPresent()) {
                return foundNode;
            }
        }

        return Optional.empty();
    }

    public void addNode(TChildNode node) {
        node.setParent(this);
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

    public void removeNode(Node<?> node) {
        // first try to remove from the current node
        if (childNodes.contains(node)) {
            assertRemoveChildNodesIsAllowed();

            if (childNodes.size() == 1) {
                assertRemoveLastChildNodeIsAllowed();
            }

            node.setParent(null);
            childNodes.remove(node);
            return;
        }

        // if not found, try to remove from the child nodes
        for (TChildNode childNode : childNodes) {
            childNode.removeNode(node);
        }
    }

    public void removeNodes() {
        assertRemoveChildNodesIsAllowed();
        assertRemoveLastChildNodeIsAllowed();

        for (TChildNode node : childNodes) {
            node.setParent(null);
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
        Node<?> node = (Node<?>) o;
        return getId() != null && Objects.equals(getId(), node.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
