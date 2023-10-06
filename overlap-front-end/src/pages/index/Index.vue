<template>

  <top-search-router-bar :placeholder="'请输入用户名 / 手机号 / 邮箱 / 标签'" :path="'/user/list'"/>

  <!--    顶部标签-->
  <!--  sticky 开启粘性布局-->
  <!--  swipeable 开启滑动切换-->
  <van-tabs v-model:active="active"  color="red" sticky :offset-top="54">
    <van-tab title="推荐" name="recommend">
      <Recommend/>
    </van-tab>
    <van-tab title="分类" name="categories">
      <Categories/>
    </van-tab>
    <van-tab title="游戏" name="games">
      <Games/>
    </van-tab>
  </van-tabs>
</template>

<script setup>
import {ref} from 'vue';
import Recommend from "./Recommend.vue";
import Games from "../games/GamesIndex.vue";
import {useRouter} from "vue-router";
import Categories from "./Categories.vue";
import TopSearchRouterBar from "../../components/bar/TopSearchRouterBar.vue";

const router = useRouter()
const active = ref('recommend');


</script>

<style scoped>
</style>