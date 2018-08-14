package org.openrsc.server.event;

import org.openrsc.server.Config;
import org.openrsc.server.ServerBootstrap;
import org.openrsc.server.database.game.ChangePassword;
import org.openrsc.server.model.Player;

/**
 * @author Kenix
 */

public class ChangePasswordEvent extends TimedEvent {
    private final String newPassword;
    
	public ChangePasswordEvent(Player owner, String newPassword) {
		super(owner, 30*1000);
        this.newPassword = newPassword;
	}
	
	public void confirmPassword(String confirmedPassword){
        if(newPassword.equals(confirmedPassword)){
            // Update the password as it is confirmed.
            // TODO: This should go back to mudclient.currentPass to automatically have the password for next login
            
            ServerBootstrap.getDatabaseService().submit(new ChangePassword(owner, newPassword));
            stop();
            
            owner.sendMessage(Config.getPrefix() + " Password changed.");
        }
        else{
            owner.sendMessage(Config.getPrefix() + " Confirmation password is different from original.");
            owner.sendMessage(Config.getPrefix() + " Please try again.");
        }
    }
    
    public void onComplete() {
	    owner.sendMessage(Config.getPrefix() + " Password not changed.");
	    owner.sendMessage(Config.getPrefix() + " Type ::changepassword [new_password] to try again.");
    }
}
