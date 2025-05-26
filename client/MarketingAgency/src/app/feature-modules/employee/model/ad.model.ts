export interface Ad {
    id: number;
    clientId: number;
    employeeId: number;
    slogan: string;
    description: string;
    duration: number;
    posted: number;
    requestId: number;
    clientName?: string;
}
  