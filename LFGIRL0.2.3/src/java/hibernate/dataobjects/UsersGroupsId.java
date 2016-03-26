package hibernate.dataobjects;
// Generated 25-Mar-2016 11:25:17 PM by Hibernate Tools 4.3.1


import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * UsersGroupsId generated by hbm2java
 */
@Embeddable
public class UsersGroupsId  implements java.io.Serializable {


     private int userId;
     private int groupId;

    public UsersGroupsId() {
    }

    public UsersGroupsId(int userId, int groupId) {
       this.userId = userId;
       this.groupId = groupId;
    }
   


    @Column(name="user_id", nullable=false)
    public int getUserId() {
        return this.userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }


    @Column(name="group_id", nullable=false)
    public int getGroupId() {
        return this.groupId;
    }
    
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof UsersGroupsId) ) return false;
		 UsersGroupsId castOther = ( UsersGroupsId ) other; 
         
		 return (this.getUserId()==castOther.getUserId())
 && (this.getGroupId()==castOther.getGroupId());
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + this.getUserId();
         result = 37 * result + this.getGroupId();
         return result;
   }   


}


