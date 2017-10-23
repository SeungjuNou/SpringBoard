package member.service;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import member.dao.MemberDao;
import model.member.MemberModel;

public class MemberService implements MemberDao{
	private SqlMapClientTemplate sqlMapClientTemplate;
	
	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
		this.sqlMapClientTemplate = sqlMapClientTemplate;
	}

	@Override
	public boolean addMember(MemberModel memberModel) {

		//member 데이터베이스에 삽입
		sqlMapClientTemplate.insert("member.addMember", memberModel);

		//가입후에 데이터가 삽입되었는지 체크 
		MemberModel checkAddMember = findByUserId(memberModel.getUserId());
		
		//데이터 조회결과가 없다면 데이터삽입오류 false리턴.
		if(checkAddMember == null){
			return false;
		} else {
			return true;
		}
	}

	@Override
	public MemberModel findByUserId(String userId) {
		//해당하는 id를 데이터베이스에서 찾아옴.
		return (MemberModel) sqlMapClientTemplate.queryForObject("member.findByUserId", userId);
	}

}