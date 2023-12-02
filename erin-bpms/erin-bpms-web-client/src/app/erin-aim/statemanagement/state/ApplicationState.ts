import {AuthModel} from '../../authentication/models/auth.model';

export type ApplicationState = Readonly<{
  auth: AuthModel;
}>;
