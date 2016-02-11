package hibernate.dataobjects;
// Generated Feb 8, 2016 7:33:23 PM by Hibernate Tools 4.3.1


import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * UsersGroups generated by hbm2java
 */
@Entity
@Table(name="users_groups"
    ,catalog="mydb"
)
public class UsersGroups  implements java.io.Serializable {


     private UsersGroupsId id;
     private Groups groups;
     //@JsonManagedReference
     private Users users;
     private Boolean isLeader;

    public UsersGroups() {
    }

	
    public UsersGroups(UsersGroupsId id, Groups groups, Users users) {
        this.id = id;
        this.groups = groups;
        this.users = users;
    }
    public UsersGroups(UsersGroupsId id, Groups groups, Users users, Boolean isLeader) {
       this.id = id;
       this.groups = groups;
       this.users = users;
       this.isLeader = isLeader;
    }
   
     @EmbeddedId

    
    @AttributeOverrides( {
        @AttributeOverride(name="userId", column=@Column(name="user_id", nullable=false) ), 
        @AttributeOverride(name="groupId", column=@Column(name="group_id", nullable=false) ) } )
    public UsersGroupsId getId() {
        return this.id;
    }
    
    public void setId(UsersGroupsId id) {
        this.id = id;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="group_id", nullable=false, insertable=false, updatable=false)
    public Groups getGroups() {
        return this.groups;
    }
    
    public void setGroups(Groups groups) {
        this.groups = groups;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, insertable=false, updatable=false)
    public Users getUsers() {
        return this.users;
    }
    
    public void setUsers(Users users) {
        this.users = users;
    }

    
    @Column(name="is_leader")
    public Boolean getIsLeader() {
        return this.isLeader;
    }
    
    public void setIsLeader(Boolean isLeader) {
        this.isLeader = isLeader;
    }




}


