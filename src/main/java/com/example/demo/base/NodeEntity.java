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
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Base class for a node entity.
 * <p>
 * A node entity is a node in a tree structure.
 * It can have child nodes.
 * It is used for questions, answers, and other nodes.
 * The node entity is a base class for all nodes.
 * Every subclass must have a {@link DiscriminatorValue} annotation, so we explicitly define the dtype (benefit: the class name is not relevant, and can be changed without breaking anything).
 *
 * @param <TChildNode> the type of the child nodes - has to be a subclass of {@link NodeEntity}
 */
@Slf4j
@Getter
// only protected setter allowed, so no setter outside the package in domain driven design
@Setter(AccessLevel.PROTECTED)

// Configures jackson to use the dtype defined by {@link DiscriminatorValue} as type identifier
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "dtype")

// This tells Hibernate to create the dtype column
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)

// InheritanceType.JOINED is used to create a separate table for each subclass
@Inheritance(strategy = InheritanceType.JOINED)

// db table name
@Entity(name = "node")
// db table indexes
@Table(indexes = {
        @Index(name = "idx_node_parent_id", columnList = "parent_id"),
        @Index(name = "idx_node_dtype", columnList = "dtype")
})
public abstract class NodeEntity<TChildNode extends NodeEntity<?>> {

    // no setter allowed, the id is defined in the init method
    @Setter(AccessLevel.NONE)
    @Id
    private UUID id;

    /**
     * Discriminator column to store the dtype
     */
    @Column(insertable = false, updatable = false)
    private String dtype;

    /**
     * Reference to the parent node, if applicable
     */
    @JsonIgnore // ignore the parent node in the json response
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // Foreign key to the parent node
    protected NodeEntity<?> parent;

    @Setter(AccessLevel.PROTECTED) // no setter allowed, hibernate will set the parent using reflection
    @Column(name = "parent_id", insertable = false, updatable = false)
    private UUID parentId;

    /**
     * The child nodes of the node
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY) // only include this list in json if not empty
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, targetEntity = NodeEntity.class)
    @BatchSize(size = 50) // optimize the batch size for the child nodes -> reduce the number of queries
    @OrderBy("createdAt")
    private List<TChildNode> childNodes = new ArrayList<>();

    @JsonIgnore
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Initializes the node before it is first time stored in the database.
     */
    @PrePersist
    @PreUpdate
    final protected void init() {
        // initialize the node only once and do this only if the id is defined by hibernate
        if (id == null) {
            id = UUID.randomUUID();
            initializeNode();
            updateTypeFromDiscriminator();
            log.debug("INIT NODE: {} - dtype '{}'", this.getClass().getSimpleName(), this.dtype, this.id);
        }

        // check if parent defined when not root node
        if (!isRootNode() && parent == null) {
            throw new IllegalArgumentException("Parent node is not defined");
        }

        initChildNodes();
    }

    /**
     * Clean up the node before it is removed from the database.
     */
    @PreRemove
    final protected void destroy() {
        log.debug("DESTROY NODE: {} - dtype '{}'", this.getClass().getSimpleName(), this.dtype, this.id);
        onDestroyNode();
    }

    /**
     * Trigger the "PrePersist"-init method on the child nodes before it is stored in the database.
     * <p>
     * This is needed because the "PrePersist" method is not called on the child nodes when they are added to the parent node.
     */
    private void initChildNodes() {
        for (TChildNode childNode : childNodes) {
            childNode.init();
        }
    }

    /**
     * Update the dtype from the DiscriminatorValue annotation
     * <p>
     * <strong>IMPORTANT</strong>: Every subclass must have a DiscriminatorValue annotation
     */
    private void updateTypeFromDiscriminator() {
        DiscriminatorValue discriminatorValue = this.getClass().getAnnotation(DiscriminatorValue.class);
        if (discriminatorValue != null) {
            this.dtype = discriminatorValue.value();
        } else {
            throw new IllegalArgumentException("DiscriminatorValue annotation is missing");
        }
    }

    /**
     * Update the node itself with the given data (not the child nodes). This method is called by {@link #update(NodeEntity)}.
     * The given data is of the same type as the node.
     *
     * @param nodeEntity the nodeEntity to update
     * @return change log // TODO: change log return type
     */
    protected abstract String updateNode(NodeEntity<?> nodeEntity);

    /**
     * Create a new child node.
     * <p>
     * If no child node is allowed, return an empty Optional
     *
     * @return the new child node or an empty Optional
     */
    protected abstract Optional<TChildNode> createNewChildNode();

    /**
     * Adds a new child node by calling {@link #createNewChildNode()} and then {@link #addChildNode(NodeEntity)}.
     * <p>
     * If no child node was created by {@link #createNewChildNode()}, an empty Optional is returned.
     *
     * @return the new child node or an empty Optional
     */
    final public Optional<TChildNode> addNewChildNode() {
        Optional<TChildNode> newChildNodeOptional = createNewChildNode();

        if (newChildNodeOptional.isEmpty()) {
            return Optional.empty();
        }

        addChildNode(newChildNodeOptional.get());
        return newChildNodeOptional;
    }

    /**
     * Override this method and return true if this node is the root node.
     * <p>
     * default is false: the node can have a parent node
     * <p>
     * If this method returns true, setting the parent node is not allowed.
     *
     * @return true if this node is the root node (default is false)
     */
    @JsonIgnore
    public boolean isRootNode() {
        return false;
    }

    /**
     * This method is called only once from the init method.
     * <p>
     * Override this method to set up the node if needed
     * <p>
     * <b>IMPORTANT</b>: Don't call this method directly, it is called by the init method.
     * <p>
     * E.g. to set up child nodes (add initial answers or questions), document uploads, etc.
     */
    protected void initializeNode() {
        // override this method to set up the node if needed
    }

    /**
     * This method is called before the node is removed.
     * <p>
     * Override this method to clean up the node if needed
     * <p>
     * <b>IMPORTANT</b>: Don't call this method directly, it is called automatically.
     * <p>
     * Example implementation: remove document uploads and trigger domain event for them
     */
    protected void onDestroyNode() {
        // override this method to clean up the node if needed
    }

    /**
     * Override this method if you want to prevent the last child node from being removed on special conditions.
     * E.g. if at least one answer must be present for a yes/no question.
     *
     * @return true if the last child node can be removed
     * @see #isRemoveChildNodesAllowed()
     */
    protected boolean isRemoveLastChildNodeAllowed() {
        return true;
    }

    /**
     * Override this method if you want to prevent the removal of child nodes.
     * <p>
     * By default, child nodes can be removed.
     *
     * @return true if child nodes can be removed (default is true)
     */
    protected boolean isRemoveChildNodesAllowed() {
        return true;
    }

    /**
     * Override this method if you want to prevent child nodes from being added.
     * <p>
     * By default, child nodes can be added.
     *
     * @return true if child nodes can be added (default is true)
     */
    protected boolean isAddingChildNodesAllowed() {
        return true;
    }

    /**
     * Retuns a read-only list of the child nodes.
     */
    public List<TChildNode> getChildNodes() {
        return childNodes.stream().toList();
    }

    /**
     * Update the node with the given data. Also updates the child nodes if existing.
     *
     * @param nodeEntity the new data to update the node from - must be of the same type
     * @return change log // TODO: change log return type
     */
    final public String update(NodeEntity<?> nodeEntity) {
        log.info(this.updateNode(nodeEntity));
        return updateChildNodes(nodeEntity);
    }

    /**
     * Update only the child nodes of the node with the given data. The node itself is not updated.
     * <p>
     * It gets the child nodes from the given nodeEntity and updates the existing child nodes.
     * It won't add or remove child nodes, only update the existing ones.
     *
     * @param updateNodeEntity the new data to update the child nodes from - must be of the same type as the current node.
     * @return change log // TODO: change log return type
     */
    final public String updateChildNodes(NodeEntity<?> updateNodeEntity) {
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
     * Find a nodeEntity by id. Search also in the child nodes.
     *
     * @param nodeEntity the node with id to find. If the id is null, the method returns an empty Optional.
     * @return the node if found, otherwise empty Optional
     */
    final public Optional<NodeEntity<?>> findNode(NodeEntity<?> nodeEntity) {
        // find the nodeEntity by id recursively
        if (nodeEntity == null || nodeEntity.getId() == null) {
            return Optional.empty();
        }

        return findNodeById(nodeEntity.getId());
    }

    /**
     * Find a node by id recursively
     */
    final public Optional<NodeEntity<?>> findNodeById(UUID id) {
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

    /**
     * Add a node to the child nodes if allowed.
     *
     * @param node the node to add
     */
    final public void addChildNode(TChildNode node) {
        assertAddChildNodeIsAllowed();

        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }

        setSelfAsParentToNode(node);
        childNodes.add(node);
        log.debug("ADD NODE {} - dtype '{}'", node.getClass().getSimpleName(), node.getDtype());
    }

    /**
     * Remove a node by id recursively
     *
     * @param id the id of the node to remove
     * @return true if the node was removed, otherwise false
     */
    final public boolean removeNodeById(UUID id) {
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

    /**
     * Remove a node from the child nodes if allowed.
     *
     * @param nodeEntity the node to remove
     * @return change log // TODO: change log return type
     */
    final public String removeNode(NodeEntity<?> nodeEntity) {
        // first try to remove from the current nodeEntity
        if (childNodes.contains(nodeEntity)) {
            assertRemoveChildNodesIsAllowed();

            if (childNodes.size() == 1) {
                assertRemoveLastChildNodeIsAllowed();
            }

            childNodes.remove(nodeEntity);
            removeParentFromNode(nodeEntity);
            // TODO: Domain Event for node removed
            return "removed node: " + nodeEntity.getId();
        }

        // if not found, try to remove from the child nodes
        String changeLog = "";
        for (TChildNode childNode : childNodes) {
            changeLog += childNode.removeNode(nodeEntity);
        }
        return changeLog;
    }

    /**
     * Get all child nodes recursively as a list.
     *
     * @return a list of all child nodes
     */
    @JsonIgnore
    public List<NodeEntity<?>> getAllChildNodes() {
        List<NodeEntity<?>> allChildNodes = new ArrayList<>();
        for (TChildNode childNode : childNodes) {
            allChildNodes.add(childNode);
            allChildNodes.addAll(childNode.getAllChildNodes());
        }
        return allChildNodes;
    }

    /**
     * Remove all child nodes if allowed.
     * <p>
     * Checks if the child nodes can be removed of the current node.
     * If allowed, the complete subtree of each child is removed.
     *
     * @return change log // TODO: change log return type
     * @see #isRemoveChildNodesAllowed()
     * @see #isRemoveLastChildNodeAllowed()
     */
    final public String removeChildNodes() {
        assertRemoveChildNodesIsAllowed();
        assertRemoveLastChildNodeIsAllowed();

        return removeChildNodesOfRemovedNode();
    }

    /**
     * Remove all child nodes of the removed node. This method is called recursively.
     * <p>
     * No checks are done here, because the checks are done in the calling public method.
     *
     * @return change log // TODO: change log return type
     */
    final protected String removeChildNodesOfRemovedNode() {
        StringBuilder changeLog = new StringBuilder();
        for (TChildNode node : childNodes) {
            removeParentFromNode(node);
            changeLog.append(node.removeChildNodesOfRemovedNode());
        }
        childNodes.clear();
        return changeLog.toString();
    }

    private void setSelfAsParentToNode(NodeEntity<?> node) {
        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }
        if (node == this) {
            throw new IllegalArgumentException("Cannot set myself as parent");
        }
        node.setParent(this);
        node.setParentId(this.getId());
    }

    private void removeParentFromNode(NodeEntity<?> node) {
        node.setParent(null);
        node.setParentId(null);
    }

    private void assertAddChildNodeIsAllowed() {
        if (!isAddingChildNodesAllowed()) {
            throw new IllegalArgumentException("Cannot add child node");
        }
    }

    private void assertRemoveChildNodesIsAllowed() {
        if (!isRemoveChildNodesAllowed()) {
            throw new IllegalArgumentException("Cannot remove child nodes on this node %s".formatted(this.dtype));
        }
    }

    private void assertRemoveLastChildNodeIsAllowed() {
        if (!isRemoveLastChildNodeAllowed()) {
            throw new IllegalArgumentException("Cannot remove the last child node");
        }
    }

    public void setParent(NodeEntity<?> parent) {
        if (isRootNode()) {
            throw new IllegalArgumentException("Cannot set parent node for root node");
        }

        this.parent = parent;
        this.parentId = parent != null ? parent.getId() : null;
    }

    public void setParentId(UUID parentId) {
        if (isRootNode()) {
            throw new IllegalArgumentException("Cannot set parent node for root node");
        }

        this.parentId = parentId;
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
