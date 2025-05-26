export class RequestAdvertisement {
    id: number;
    userId:number;
    deadlineDate: Date;
    activeFrom: Date;
    activeTo: Date;
    description: string;
    adCreated?: boolean;
}
  