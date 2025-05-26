export interface Registration {
    name: string,
    surname: string,
    email: string,
    phoneNumber: string,
    city: string,
    country: string,
    adress: string,
    password: string,
    packageType: string,
    role: string[] | null,
}