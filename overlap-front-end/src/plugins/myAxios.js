// 创建实例时配置默认值
import axios from "axios";

//npm i --save-dev @types/node

const isDev = process.env.NODE_ENV === 'development'

const myAxios = axios.create({
    baseURL: isDev ? 'http://localhost:8081/api' : 'http://overlap-backend.ovta.love:8081/api',
    withCredentials: true   //  配置携带凭证（携带 cookie）
});

myAxios.defaults.withCredentials = true

// 添加请求拦截器
myAxios.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
myAxios.interceptors.response.use(function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    return response;
}, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
});

export default myAxios