import * as auth from '../../actions/auth/auth';
import {AuthModel} from '../../../authentication/models/auth.model';

export function authReducer(state: AuthModel, action: auth.Actions) {
  switch (action.type) {
    case auth.ActionTypes.AUTH_SET:
      return action.payload.auth;
    case auth.ActionTypes.AUTH_CLEAR:
      return  state = new AuthModel();
    default:
      return state;
  }
}
