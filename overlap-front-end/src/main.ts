import { createApp } from 'vue'
import App from './App.vue'
import {
    Button,
    Cell,
    CellGroup,
    DropdownItem,
    DropdownMenu,
    Icon,
    NavBar,
    Search, Skeleton, SkeletonAvatar, SkeletonImage, SkeletonParagraph, SkeletonTitle,
    Tab,
    Tabbar,
    TabbarItem,
    Tabs
} from 'vant';
import * as VueRouter from 'vue-router'
import routes from "./config/route";
import {createPinia} from "pinia";

// Toast
import { showToast } from 'vant';
import 'vant/es/toast/style';

// Dialog
import { showDialog } from 'vant';
import 'vant/es/dialog/style';

// Notify
import { showNotify } from 'vant';
import 'vant/es/notify/style';

// ImagePreview
import { showImagePreview } from 'vant';
import 'vant/es/image-preview/style';

const app = createApp(App);
const pinia = createPinia()


// 创建路由实例并传递 `routes` 配置
const router = VueRouter.createRouter({
    history: VueRouter.createWebHistory(),
    routes, // `routes: routes` 的缩写
})
//确保 _use_ 路由实例使
//整个应用支持路由。
app.use(router)

// 通过 app.use 注册
app.use(Button);
app.use(NavBar);
app.use(Icon);
app.use(Tabbar);
app.use(TabbarItem);
app.use(Search);
app.use(Tab);
app.use(Tabs);
app.use(DropdownMenu);
app.use(DropdownItem);
app.use(Cell);
app.use(CellGroup);
app.use(Skeleton);
app.use(SkeletonTitle);
app.use(SkeletonImage);
app.use(SkeletonAvatar);
app.use(SkeletonParagraph);
app.use(pinia);

app.mount('#app')