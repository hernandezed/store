import { IUser } from 'app/shared/model/user.model';
import { IOrder } from 'app/shared/model/order.model';

export interface ICustomer {
  id?: number;
  name?: string;
  email?: string;
  phone?: string;
  addressLine1?: string;
  addressLine2?: string | null;
  city?: string;
  user?: IUser;
  orders?: IOrder[] | null;
}

export const defaultValue: Readonly<ICustomer> = {};
