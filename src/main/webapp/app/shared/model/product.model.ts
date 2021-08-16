import { IProductCategory } from 'app/shared/model/product-category.model';
import { ProductStatus } from 'app/shared/model/enumerations/product-status.model';

export interface IProduct {
  id?: number;
  name?: string;
  description?: string | null;
  quantityPerPackage?: number;
  price?: number;
  imageContentType?: string | null;
  image?: string | null;
  stock?: number;
  status?: ProductStatus;
  productCategory?: IProductCategory | null;
}

export const defaultValue: Readonly<IProduct> = {};
