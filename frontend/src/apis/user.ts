import { API_PATH } from 'constants/path';

import { User } from 'types/user';

import { axios } from './axios';

const requestLogin = (userData: User) => {
  return axios.post(API_PATH.LOGIN, userData).then(res => res.data);
};

export { requestLogin };
