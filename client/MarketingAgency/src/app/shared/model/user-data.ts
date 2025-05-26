
export interface UserData{
    id?: number;
    email: string;
    password: string;
    name: string;
    surname: string;
    phoneNumber: string;
    adress: string;
    city: string;
    country: string;
    isVerified: boolean;
    packageType: EPackage;
    status: EUserStatus;
    roles?: Role[];
    clickedApproved?: boolean,
    firstTimeLogin?: boolean
  }
  
  export enum EPackage {
    BASIC,
    STANDARD,
    GOLDEN
  }
  
  export enum EUserStatus {
    PENDING,
    ACTIVE,
    REJECTED
  }
  
  export interface Role {
    id?: number;
    name: ERole;
  }

  export enum ERole{
    ROLE_USER,
    ROLE_MODERATOR,
    ROLE_ADMIN
  }