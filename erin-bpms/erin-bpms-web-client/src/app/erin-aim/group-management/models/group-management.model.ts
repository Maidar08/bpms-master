export interface DetailedUserInfo {
  username: string;
  email: string;
  phoneNumber: string;
  membership: Membership;
}

export interface UserInfo {
  username: string;
  email: string;
  phoneNumber: string;
  role?: string;
  moreOption?: boolean;
}
export interface Membership {
  userId: string;
  groupId: string;
  membershipId: string;
  roleId: string;
}
export interface AddUserModel {
  roleId: string;
  users: string[];
}

export interface Role {
  roleId: string;
  roleName: string;
}
export interface Group {
  id: string;
  name: string;
}
