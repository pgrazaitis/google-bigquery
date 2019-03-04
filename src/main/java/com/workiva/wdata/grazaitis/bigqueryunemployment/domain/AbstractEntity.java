package com.workiva.wdata.grazaitis.bigqueryunemployment.domain;

import java.util.UUID;

public abstract class AbstractEntity {
    UUID objectId = UUID.randomUUID();

    public UUID get_objectId() { return objectId; }

    @Override
    public int hashCode() {
        return get_objectId().hashCode();
    }

}
