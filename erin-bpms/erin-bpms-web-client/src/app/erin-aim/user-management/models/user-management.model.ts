export interface UserDialogData {
  title: string;
  type: string;
  user: UserModel;
}

export interface ConfirmationDialogData {
  title: string;
  body: string;
}

export interface UserModel {
  userId: string;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  gender: Gender;
  password: string;
  confirmPassword: string;
  status: UserStatus;
  dateLastModified: any;
  message?: string;
  deletable?: boolean;
}

export interface ArchiveUserModel {
  userIds: string[];
  archived: boolean;
}

export enum Gender {
  MALE, FEMALE, NA
}

export enum UserStatus {
  ACTIVE, ARCHIVED
}

export interface ColumnDef {
  columnDef: string;
  headerText: string;
  progress?: boolean;
}

export const USER_TABLE_COLUMNS: ColumnDef[] = [
  {columnDef: 'username', headerText: 'Хэрэглэгчийн нэр'},
  {columnDef: 'firstName', headerText: 'Нэр'},
  {columnDef: 'lastName', headerText: 'Овог'},
  {columnDef: 'email', headerText: 'Имэйл'},
  {columnDef: 'phoneNumber', headerText: 'Утас'},
  {columnDef: 'dateLastModified', headerText: 'Өөрчилсөн огноо'},
  {columnDef: 'status', headerText: 'Статус'},
];
