import Axios from 'axios';

const baseURL = 'localhost:3000';

export const axios = Axios.create({ baseURL });
