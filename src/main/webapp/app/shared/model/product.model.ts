import { IProductCategory } from 'app/shared/model/product-category.model';

export interface IProduct {
  id?: number;
  name?: string;
  description?: string | null;
  quantityPerPackage?: number;
  price?: number;
  imageContentType?: string | null;
  image?: string | null;
  stock?: number;
  productCategory?: IProductCategory | null;
}

export const defaultValue: Readonly<IProduct> = {};
