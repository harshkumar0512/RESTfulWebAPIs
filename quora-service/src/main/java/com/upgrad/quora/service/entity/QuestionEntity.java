package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "QUESTION")
@NamedQueries({
        @NamedQuery(name = "QuestionEntityByUuid", query = "SELECT q FROM QuestionEntity q WHERE q.uuid = :uuid"),
        @NamedQuery(name = "QuestionEntityByid", query = "SELECT q FROM QuestionEntity q WHERE q.id = :id"),
        @NamedQuery(name = "getAllQuestions", query = "SELECT q FROM QuestionEntity q"),
        @NamedQuery(name = "QuestionEntityByUserId", query = "SELECT q FROM QuestionEntity q WHERE q.user = :user")
})
public class QuestionEntity implements Serializable {

    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "UUID")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "content")
    @Size(max = 500)
    @NotNull
    private String content;

    @Column(name = "date")
    @NotNull
    private ZonedDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public UserEntity getUser_id() {
        return user;
    }

    public void setUser_id(UserEntity user_id) {
        this.user = user_id;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
