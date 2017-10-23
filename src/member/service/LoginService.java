package member.service;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import member.dao.LoginDao;
import model.login.LoginSessionModel;

public class LoginService implements LoginDao {
	private SqlMapClientTemplate sqlMapClientTemplate;
	
	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
		this.sqlMapClientTemplate = sqlMapClientTemplate;
	}

	@Override
	public LoginSessionModel checkUserId(String userId) {
		return (LoginSessionModel) sqlMapClientTemplate.queryForObject("login.loginCheck", userId);

	}	
}
