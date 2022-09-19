package tech.tresearchgroup.palila.model;

import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;

import java.util.Date;

public class BasicObject implements BasicObjectInterface {
    private transient Date created;

    private transient Date updated;

    private Long id;

    public BasicObject() {
    }

    public BasicObject(Date created, Date updated, Long id) {
        this.created = created;
        this.updated = updated;
        this.id = id;
    }

    @Serialize(order = 0)
    @SerializeNullable
    public Date getCreated() {
        return created;
    }

    @Serialize(order = 1)
    @SerializeNullable
    public Date getUpdated() {
        return updated;
    }

    @Serialize(order = 2)
    @SerializeNullable
    public Long getId() {
        return id;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
