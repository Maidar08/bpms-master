import {type} from '../../util/util';
import {Action} from '@ngrx/store';
import {AuthModel} from '../../../authentication/models/auth.model';


export const ActionTypes = {
  AUTH_SET: type<'AUTH_SET'>('AUTH_SET'),
  AUTH_CLEAR: type<'AUTH_CLEAR'>('AUTH_CLEAR'),
};



export class ClearAuth implements Action {
  type = ActionTypes.AUTH_CLEAR;
}


export class SetAuth implements Action {
  type = ActionTypes.AUTH_SET;
  payload: Readonly<{ auth: AuthModel }>;

  constructor(auth: AuthModel) {
    this.payload = {auth};
  }
}

export type Actions = ClearAuth | SetAuth;


