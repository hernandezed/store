import dayjs from 'dayjs';
import { IInvoice } from 'app/shared/model/invoice.model';
import { IOrderItem } from 'app/shared/model/order-item.model';
import { IUser } from 'app/shared/model/user.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';

export interface IOrder {
  id?: number;
  placedDate?: string;
  status?: OrderStatus;
  code?: string;
  invoice?: IInvoice | null;
  orderItems?: IOrderItem[] | null;
  user?: IUser;
  customer?: ICustomer;
}

export const defaultValue: Readonly<IOrder> = {};
