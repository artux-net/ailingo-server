package org.ailingo.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    protected UUID id;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BaseEntity that = (BaseEntity) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        if (id == null) return super.hashCode();
        else
            return id.hashCode();
    }
}
