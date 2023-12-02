import {AimConfig} from './erin-aim/aim.config';
import {environment} from '../environments/environment';

export const AIM_CONFIG: AimConfig = {tenantId: environment.tenantId, baseUrl: environment.baseUrl};
