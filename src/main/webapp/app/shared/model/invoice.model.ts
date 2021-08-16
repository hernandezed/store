import dayjs from 'dayjs';
import { IOrder } from 'app/shared/model/order.model';
import { InvoiceStatus } from 'app/shared/model/enumerations/invoice-status.model';

export interface IInvoice {
  id?: number;
  code?: string;
  date?: string;
  details?: string | null;
  status?: InvoiceStatus;
  paymentAmount?: number;
  order?: IOrder | null;
}

export const defaultValue: Readonly<IInvoice> = {};
