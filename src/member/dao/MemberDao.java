package member.dao;

import model.member.MemberModel;

public interface MemberDao {
	boolean addMember(MemberModel memberModel);
	MemberModel findByUserId(String userId);
}