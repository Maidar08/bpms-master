export class AuthModel {
  userName: string;
  role: string;
  permissions: PermissionItem[];
  userGroup: string;
}

export class RoleProperties {
  roleName: string;
  roleUrl: string;
  role: string;
}

export interface PermissionItem {
  id: string;
  properties: any;
}

export interface Properties {
  disable: boolean;
  visible: boolean;
}

export interface GroupMembers {
  groupName: string;
  users: User[];
}

export interface User {
  userId: string;
  firstName: string;
  lastName: string;
}
