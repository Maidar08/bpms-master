import {AuthModel} from "../../../authentication/models/auth.model";
import * as actions from '../../actions/auth/auth';
import * as auth from './auth.reducer';


describe('auth reducer', () => {
  let testAuth: AuthModel = {
    role: 'admin',
    userName: 'M.Flower',
    permissions: [{id: 'app.id', properties: {visible: true, disable: true}}],
    userGroup: 'group'
  };
  beforeEach(() => {
  });

  it('should set new model state', () => {
    const state = auth.authReducer(undefined, new actions.SetAuth(testAuth));
    expect(state.userName).toBe('M.Flower');
    expect(state.userGroup).toBe('group');
    expect(state.role).toBe('admin');
    expect(state.permissions).toEqual(testAuth.permissions);
  });

  it('should clear state', () => {
    const state = auth.authReducer(testAuth, new actions.ClearAuth());
    expect(state).not.toBeUndefined();
    expect(state.userName).toBeUndefined();
    expect(state.userGroup).toBeUndefined();
    expect(state.permissions).toBeUndefined();
  });
});
