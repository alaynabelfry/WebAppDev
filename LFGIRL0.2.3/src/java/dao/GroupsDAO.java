/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.dataobjects.Groups;
import hibernate.dataobjects.Users;
import hibernate.dataobjects.UsersGroups;
import java.util.*;

/**
 *
 * @author Protostar
 */
public interface GroupsDAO {
    
    public List<Groups> listGroups();
    
    public Groups findGroupById(int id);
    
    public List<Groups> findGroupsByUserId(int userId);
    
    public Groups findGroupByName(String groupName);
    
    public List<Groups> findGroupsByName(String groupName);
    
    public List<Groups> findGroupsByDesc(String desc);
    
    public void addGroup(Groups g);
    
    public void updateGroup(Groups g);
    
    /*added String s as parameter @yawei*/
    public boolean groupCheck(String s);

    public void addMember(UsersGroups ug);

    public void deleteGroup(int gid);

   //public void deleteMember(UsersGroupsId ugid);
    
    //try to remove member from group @yawei
    public void deleteMember(Users user, Groups group); 
    
    //try to implment group leader search @yawei
    public  String findGroupLeaedr(int gid);
    
    //search for members of a group @yawei
    public List<Users> findGroupMembers(int gid);
}
