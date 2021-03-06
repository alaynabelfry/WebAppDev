/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backingbeans;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import hibernate.dataobjects.Groups;
import hibernate.dataobjects.Tags;
import hibernate.dataobjects.Users;
import hibernate.dataobjects.UsersGroups;
import hibernate.dataobjects.UsersGroupsId;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletContext;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;
import serializer.Autowirer;
import services.interfaces.GroupsService;
import services.interfaces.UsersService;
import services.interfaces.GroupMessageService;

/**
 *
 * @author Alayna
 */
@ManagedBean(name = "GroupBean", eager=true)
@ViewScoped
public class GroupBean implements Serializable{
    @Autowired
    transient GroupsService groupsService;
    
    @Autowired
    transient UsersService usersService;
    
    @Autowired
    transient GroupMessageService groupMessageService;
    
    private int gid;
    private int uid;
    private Groups group;
    private Boolean updateFlag;
    private Boolean isLeader;
    private Boolean isMember = false; //try to implement join group funtion @yawei
    private Boolean isUser = false; //try to implement join group funtion @yawei
    private Set<Tags> tags=null;//temporary list of tags @alayna
    private String newTagName="";
    
   @PostConstruct
    private void init() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ServletContext sC = (ServletContext) ec.getContext();
        WebApplicationContextUtils.getRequiredWebApplicationContext(sC).getAutowireCapableBeanFactory().autowireBean(this);
                
        if(!FacesContext.getCurrentInstance().getViewRoot().getViewId().contains("groupCreate"))
            setGID();
        else 
            group=new Groups();
    }
    
    private void setGID(){
        String tempId=FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gid");
        gid=Integer.parseInt(tempId);
        group=groupsService.findGroupById(gid);
        updateFlag=false;
        LoginBean user = (LoginBean)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("LoginBean");
        
        if(user!=null){
            Set<UsersGroups> members = group.getUsersGroupses();
            for(UsersGroups member:members){
                if(member.getUsers().getUserId()==user.getUserId()){
                    setIsLeader(member.getIsLeader());
                    setIsMember(true); //try to implement join group funtion @yawei
                }
            }
        }
        
        if(isLeader==null)
            setIsLeader(false);
        
        if(isLeader==false && FacesContext.getCurrentInstance().getViewRoot().getViewId().contains("groupEdit")){
            String groupPage=String.format("groupProfile.xhtml?gid=%d", gid);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(groupPage);
            } catch (IOException ex) {
                Logger.getLogger(GroupBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public boolean getIsLeader(){
        return isLeader;
    }
    
    
    
    //trying to implement join group function @yawei
     public boolean getIsMember(){
        return isMember;
    }
      
    
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        Autowirer.wireObject(this);
    }
    
    public int getGid(){
        return gid;
    }
    
    
    public Groups getGroup(){
        return group;
    }
    
    public void setGroupsService(GroupsService groupService){
        this.groupsService=groupService;
    }
    
    public GroupsService getGroupService(){
        return groupsService;
    }
    
    //for group join @yawei
    public void setIsUser(boolean isUser){
      this.isUser = isUser; 
    }
    
    //for group join @yawei
    public void setIsLeader(boolean isLeader){
        this.isLeader=isLeader;
    }
    
    //for group join @yawei
     public void setUid(int uid){
         this.uid = uid;
    }
     
     //trying to implement join group function @yawei
     public void setIsMember(boolean isMember){
        this.isMember = isMember;
    }
    
    public void setUsersService(UsersService usersService){
        this.usersService=usersService;
    }
    
    public UsersService getUsersService(){
        return usersService;
    }
    
    public String getGroupName(){
        return group.getGroupname();
    }

    public void setGroupName(String name){
        if(!name.equals(getGroupName())){
            group.setGroupname(name);
            updateFlag=true;
        }
        
    }
    
    public String getDescription(){
        return group.getDescription();
    }
    
    public void setDescription(String description){
        if(!description.equals(getDescription())){
            group.setDescription(description);
            updateFlag=true;
        }
    }
    
    public String getAddress(){
        return group.getAddress();
    }
    
    public void setAddress(String address){
        if(!address.equals(getAddress())){
            SearchBean search=new SearchBean();
            search.init();
            Object[] result=search.addressToCoordinate(address);
            if(!((String)result[0]).equals("none")){
                group.setAddress((String)result[0]);
                group.setLatitude((float)result[1]);
                group.setLongitude((float)result[2]);
                updateFlag=true;
            }
        }
    }
    

    
    public void setTagsList(List<Tags> tags){
        Set tagsSet = new HashSet<>(tags);       
        group.setTagses(tagsSet);
    }
    /*converts tag set to list so that repeat and datatable can use it*/ 
    public List<Tags> getTagsList(){
        if(tags==null){
            tags=new HashSet<>(group.getTagses());
        }
        ArrayList list=new ArrayList<>(tags);
        return list;
    }
    
    public String getNewTagName(){
        return newTagName;
    }
    
    public void setNewTagName(String newTagName){
        this.newTagName=newTagName;
    }
    /*adds tag to temporary list*/
    public void addTag(){
        boolean exists=false;
        Tags tag=groupsService.findTagByName(newTagName);
        
        //search if tag has been added
        for(Tags t:tags){
            if(t.getTagName().equalsIgnoreCase(newTagName)){
                exists=true;
            }
        }
        //if the tag is already in the list, return without adding tag
        if(exists){
            newTagName="";
            return;
        }
            
        //if tag isn't in db
        if(tag==null){
            //create placeholder tag with null groups
                tag=new Tags(newTagName.toLowerCase(), null);
        } 
        tags.add(tag); 
        newTagName="";
    }
    /*remove tag from temporary list*/
    public void removeTag(Tags tag){
        tags.remove(tag);
    }
     //determine if a user can join a group @yawei
    public boolean canJoinGroup(){ 
        isUserLoggedIn();
      //can't join if not logged in   
        if(!isUser){
        return false;
        } 
      //can't join if is already leader or member
      if(getIsMember() || getIsLeader()){
        return false;    
      }
      //if user is neither leader nor member, they can join   
      return true;     
    }
    
     public boolean canLeaveGroup(){ 
        isUserLoggedIn();
      //can't leave if not logged in   
        if(!isUser){
        return false;
        } 
      //can leave if is member, but not leader
      if(getIsMember() && !getIsLeader()){
        return true;    
      }
      //if user never joined, they can't leave
      return false;     
    }
     
      public boolean canGroupMessage(){ 
        isUserLoggedIn();
      //can't leave message if not logged in   
        if(!isUser){
        return false;
        } 
      //can leave if is member, but not leader
      if(getIsMember() || getIsLeader()){
        return true;    
      }
      //if user never joined, they can't leave message
      return false;     
    }
    
    //try to check for user session @yawei
    private void isUserLoggedIn(){
        LoginBean lb = (LoginBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("LoginBean");
        if(lb!=null && lb.getUserName()!=null){
            setUid(lb.getUserId());
            setIsUser(true); //use is logged in and has uid 
        }else
         setIsUser(false); //use is not logged in      
    }
    
    //join a group by using userID and call addMember @yawei
    public void joinGroup() throws IOException{
       boolean joinSuccess = false;       
       RequestContext context = RequestContext.getCurrentInstance();  
           
           addMember(uid); //add user to group           
           group=groupsService.findGroupById(gid);         
      //make sure the user is really in the group
      Set<UsersGroups> members = group.getUsersGroupses();
            for(UsersGroups member:members){
                if(member.getUsers().getUserId()==uid){                     
                       joinSuccess = true;
                }
            }
            //join failed
            if(!joinSuccess){
              context.execute("PF('groupFailDlg').show()");
            }else{ //join success, redirect to group display page
              FacesContext.getCurrentInstance().getExternalContext().redirect("groupDisplay.xhtml");
            } 
    }
    
    public void editGroup() throws IOException{
        if(!group.getTagses().equals(tags)){
            updateFlag=true;
            /*add new tags to db and create set of tags to add to group*/
          Set<Tags> tags2=new HashSet();
            for(Tags t:tags){
                /*add new tags to db*/

                if(t.getGroupses()==null){
                    Tags tempTag=groupsService.addNewTag(t);
                    tags2.add(tempTag);
                }
                else{
                    tags2.add(t);
                }
            }
            group.setTagses(tags2);            
        }
        if(updateFlag==true)
            groupsService.updateGroupInfo(group);
        
        String groupPage=String.format("groupProfile.xhtml?gid=%d", gid);
        FacesContext.getCurrentInstance().getExternalContext().redirect(groupPage);
    }
    
    /*to be used in the group creation page, checks for existing group first, remove if needed @yawei*/
    public void addGroup(){
        RequestContext context = RequestContext.getCurrentInstance();
      //if username exists, it will show a dialog on creation page
      if(groupsService.groupCheck(group.getGroupname())){
         context.execute("PF('groupFailDlg').show()");
      } else {
        /*add new tags to db*/
          Set<Tags> tags2=new HashSet();
            for(Tags t:tags){
                /*add new tags to db*/

                if(t.getGroupses()==null){
                    Tags tempTag=groupsService.addNewTag(t);
                    tags2.add(tempTag);
                }
                else{
                    tags2.add(t);
                }
            }
            group.setTagses(tags2);                 
        
          
        groupsService.addGroup(group);
       
        List<Groups> groups=groupsService.findGroupsByName(group.getGroupname());
        
        if (groups.size()>1){//if there is more than one group returned, sort by id
           //DON'T convert to lambda experession, it will break the bean
            groups.sort(new Comparator<Groups>() {
                @Override
                public int compare(Groups g1, Groups g2) {
                    return g1.getGroupId().compareTo(g2.getGroupId());
                }
            });
            
        }
      
        //set group to the group with the highest id
        group=groups.get(groups.size()-1);
        
        if(group!=null){
            context.execute("PF('groupSuccessDlg').show()");
            gid=group.getGroupId();
            //isLeader=true;
            setIsLeader(true);
            setIsMember(true);//try to implement join group funtion @yawei
            LoginBean lv=(LoginBean)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("LoginBean");
            addMember(lv.getUserId());
        }    
        else
           context.execute("PF('groupFailDlg').show()"); 
      }
    }
    
    public void deleteGroup() throws IOException {
        System.out.println(gid);
        groupsService.deleteGroup(gid);
        FacesContext.getCurrentInstance().getExternalContext().redirect("homepage.xhtml");
    }


    public void addMember(int userID){
        Users member=usersService.getUserById(userID);
        UsersGroupsId ugid=new UsersGroupsId(userID, gid);
        UsersGroups ug;
       if(group.getUsersGroupses().isEmpty())
            ug=new UsersGroups(ugid, group, member, true);
        else
            ug=new UsersGroups(ugid, group, member, false); 
       groupsService.addMember(ug);
    }
    
    //removes a user from a group, based on group and user id@yawei
    public void removeMember() throws IOException{
        boolean leaveSuccess = true;       
        RequestContext context = RequestContext.getCurrentInstance();  
        
       //get user and groups entites for the current user
       Users member=usersService.getUserById(uid);
       Groups group = groupsService.findGroupById(gid);
       //remove all messages belongs to that user first before delete member from group
       groupMessageService.deleteUserAllMessage(uid,gid);
       groupsService.deleteMember(member, group);                   
       group=groupsService.findGroupById(gid);
      //make sure the user is really left the group
      Set<UsersGroups> members = group.getUsersGroupses();
            for(UsersGroups user:members){
                if(user.getUsers().getUserId()==uid){                     
                       leaveSuccess = false;
                }
            }
            //join Success, redirect to group display page
            if(leaveSuccess){
              FacesContext.getCurrentInstance().getExternalContext().redirect("groupDisplay.xhtml");
            }else{
              context.execute("PF('groupFailDlg').show()");            
            }  
    }
        /**
     * Creates a new instance of GroupProfileView
     */
    public GroupBean() {
        
    }  
    
   
    
    /*for group creation and editing pages, does not allow empty location*/
    public void validateAddress(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String location=value.toString();
        
        try {
            String key=groupsService.getSecretKey("googlemapsapi");
            if(key==null){
                throw new Exception("Map API key missing");
            }
            
            if(location==null||location.isEmpty()){
                throw new Exception("Invalid Location");
            }
                
            GeoApiContext apiContext = new GeoApiContext().setApiKey(key);
            GeocodingResult[] results =  GeocodingApi.geocode(apiContext,location).await();
            if(results==null||results.length==0)
               throw new Exception("Invalid Location");
        }
        catch (Exception ex) {
            FacesMessage message=new FacesMessage(ex.getMessage());
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
       
    }
}
