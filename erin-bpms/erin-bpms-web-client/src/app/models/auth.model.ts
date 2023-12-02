export interface Properties {
  disable: boolean;
  visible: boolean;
}

export interface PermissionItem {
  id: string;
  properties: Properties;
}

export interface User {
  userId: string;
  firstName: string;
  lastName: string;
}

export interface GroupMembers {
  groupName: string;
  users: User[];
}

export class AuthModel {
  userName: string;
  role: string;
  userGroup: string;
  permissions: PermissionItem[];
}

export class RoleProperties {
  roleName: string;
  roleUrl: string;
  role: string;
}

