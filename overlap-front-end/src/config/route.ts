import Index from '../pages/index/Index.vue'
import recommend from '../pages/index/Recommend.vue'
import categories from '../pages/index/Categories.vue'
import games from '../pages/games/GamesIndex.vue'
import Message from '../pages/message/Message.vue'
import ChatInterface from '../components/message/ChatInterface.vue'
import User from '../pages/user/User.vue'
import Categories from '../pages/index/Categories.vue'
import SearchUserResultPage from '../pages/index/SearchUserResultPage.vue'
import SearchCoterieResultPage from '../pages/coterie/SearchCoterieResultPage.vue'
import login from '../pages/user/Login.vue'
import register from '../pages/user/Register.vue'
import coterie from '../pages/coterie/Coterie.vue'
import updateCoterie from '../components/coterie/UpdateCoterie.vue'
import mineSweeping from '../pages/games/mineSweeping/MineSweepingIndex.vue'
import mineStar from '../pages/games/mineSweeping/MineStart.vue'

// 定义一些路由 每个路由都需要映射到一个组件。
const routes = [
    { path: '/', component: Index },
    { path: '/recommend', component: recommend },
    { path: '/categories', component: categories },
    { path: '/games', component: games },
    { path: '/message', component: Message },
    { path: '/chat', component: ChatInterface, meta: { showTabbar: false }},
    { path: '/user', component: User },
    { path: '/categories', component: Categories },
    { path: '/user/list', component: SearchUserResultPage },
    { path: '/user/login', component: login },
    { path: '/user/register', component: register },
    { path: '/coterie', component: coterie },
    { path: '/coterie/search', component: SearchCoterieResultPage },
    { path: '/coterie/update', component: updateCoterie },
    { path: '/games/mineSweeping', component: mineSweeping },
    { path: '/games/mineStar', component: mineStar },
]

export default routes