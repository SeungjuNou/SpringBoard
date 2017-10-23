package member.dao;

import model.login.LoginSessionModel;

public interface LoginDao {	
	LoginSessionModel checkUserId(String userId);
}
