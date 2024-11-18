package com.example.demo.base.documents;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Attachment {
    /**
     * ID of the document metadata in the feature service.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String ownerUserId;

    /**
     * <a href="https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/">One to Many</a>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private DocumentUploads documentUpload;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OnDelete(action = OnDeleteAction.CASCADE)
    private void onDeleteHandling() {
        System.out.println("onDeleteHandling Attachment");
    }

    /**
     * ID of the attachment in the document storage service.
     */
    private UUID documentId;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private boolean submitted = false;
    private boolean readonly = false;

    /**
     * File name of the document.
     */
    private String name;
    /**
     * MIME type of the document.
     */
    private String mimeType;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Attachment that = (Attachment) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
