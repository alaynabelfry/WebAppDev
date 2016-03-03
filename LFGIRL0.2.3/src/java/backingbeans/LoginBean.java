/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backingbeans;

import hibernate.dataobjects.Users;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;
import serializer.Autowirer;
import services.interfaces.UsersService;

/**
 *
 * @author Protostar
 */
@ManagedBean(name = "LoginBean")
@SessionScoped
public class LoginBean implements Serializable {
    
    @Autowired
    private UsersService usersService;
    
    private Users user;
    private int userId;
    private String userName;
    
    
    @PostConstruct
    private void init() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ServletContext sC = (ServletContext) ec.getContext();
        WebApplicationContextUtils.getRequiredWebApplicationContext(sC).getAutowireCapableBeanFactory().autowireBean(this);
    }
    
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        Autowirer.wireObject(this);
    }
    
    public void setUsersService(UsersService userService) {
        this.usersService = userService;
    }
    
    public UsersService getUsersService() {
        return usersService;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public void userLogin(String username, String password) {
        FacesContext fc = FacesContext.getCurrentInstance();
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage message;
        boolean loggedIn;
         
        if(username == null || password == null){
            loggedIn=false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials");
        }
        else{
            user = usersService.getUserByName(username);

            if(user != null && (password.equals(user.getPassword()))){
                //HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);
                //session.setAttribute("name", user.getUsername());
                //session.setAttribute("userId", user.getUserId());
                this.userId = user.getUserId();
                this.userName = user.getUsername();
                loggedIn = true;
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", username);
            }
            else {
                loggedIn = false;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials");
            }
        } 
        fc.addMessage(null, message);
        context.addCallbackParam("loggedIn", loggedIn);
    }
    
    public void signOut(){
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("homepage.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*Default home page message, remove or change if needed @yawei*/
    public String homePageMessage(){
      /*get property file keys for I18n*/
      FacesContext context = FacesContext.getCurrentInstance();
      ResourceBundle msg = context.getApplication().evaluateExpressionGet(context,"#{msg}", ResourceBundle.class);
      /*use msg.getString("keyname in property file") to add I18n string later*/
      if(userName == null){
        return "<html><h1>Welcome to LFGIRL, this is the place to meet up</h1></br>"  
                + "<h3>If you are a member, you can sign in to edit your personal profile, create a group and manage your groups' profiles</h3></br>"
                + "<h3>If you are new, feel free to search for groups and players. If you want to create your own group, just take a minute to sign up and start having fun!"
                +"</h3></html>";
       }
      else{
        return "<html><h1>Welcome back " +userName + ", have fun</h1></html>";  
              }
    }
}
