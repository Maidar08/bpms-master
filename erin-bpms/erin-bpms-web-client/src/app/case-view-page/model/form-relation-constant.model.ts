import {FieldOptions} from '../../models/app.model';

export interface FieldRelationModel {
  updatedFieldId: string;
  references: string[];
  operationType: string;
  operations?: string[];
  options?: Map<string, FieldOptions[]>;
}

export const MATH = 'MATH';
export const DROPDOWN = 'DROPDOWN';
export const VALIDATION = 'VALIDATION';
export const REQUIRED_VALIDATION = 'REQUIRED_VALIDATION';
export const VALIDATION_TEXT_REFERENCE = 'VALIDATION_TEXT_REFERENCE';
export const TEXT = 'TEXT';
export const ENABLE = 'enable';

export const ADD = '+';
export const SUBTRACT = '-';
export const MULTIPLY = '*';
export const DIVIDE = '/';

export const DEFAULT_VALUE = 'defaultValue';
