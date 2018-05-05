package cn.springmvc.model;

import java.io.Serializable;
import java.util.Date;

public class ProjectMember extends ProjectMemberKey implements Serializable {
    private Date createdAt;

    private String extRefId;

    private static final long serialVersionUID = 1L;

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getExtRefId() {
        return extRefId;
    }

    public void setExtRefId(String extRefId) {
        this.extRefId = extRefId == null ? null : extRefId.trim();
    }
}