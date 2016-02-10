/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.dataobjects.Groups;
import java.util.*;

/**
 *
 * @author Protostar
 */
public interface GroupsDAO {
    
    public List<Groups> listGroups();
    
    public Groups findGroupById(int id);
    
    public Groups findGroupByName(String groupName);
    
    public List<Groups> findGroupByDesc(String desc);
    
    public void addGroup(Groups g);
    
    public boolean groupCheck();
}
