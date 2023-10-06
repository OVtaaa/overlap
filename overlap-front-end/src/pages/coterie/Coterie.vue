<template>
<!--  顶部搜索条-->
  <top-search-router-bar :placeholder="'请输入圈号 / 圈名 / 标签'" :path="'/coterie/search'"/>

  <van-tabs v-model:active="active"  color="red" sticky :offset-top="54">
    <van-tab title="推荐小圈">
      <RecommendCoterie/>
    </van-tab>
    <van-tab title="我的小圈">
      <MyCoterie/>
    </van-tab>
  </van-tabs>

  <van-floating-bubble
      v-model:offset="offset"
      axis="xy"
      icon="plus"
      magnetic="x"
      @click="createCoterie"
  />

</template>


<script setup lang="ts">
import TopSearchRouterBar from "../../components/bar/TopSearchRouterBar.vue";
import {ref} from "vue";
import MyCoterie from "./MyCoterie.vue";
import RecommendCoterie from "./RecommendCoterie.vue";
import {useRouter} from "vue-router";
import {getCurrentUser} from "../../service/userService.ts";
import {showToast} from "vant";
import {CoterieType} from "../../models/coterie.d.ts";

const router = useRouter()


const active = ref(0);
const offset = ref({ x: 300, y: 540 });

const createCoterie = async () => {
  const curUser = await getCurrentUser();
  if(curUser) {
    const emptyCoterie = {
      title: "",
      tags: "[]",
      description: "",
      isEncrypted: 0,
      password: "",
      status: 0,
      maxNum: 1,
    };
    await router.push({
      path: "/coterie/update",
      query: {
        title: "创建小圈",
        backEndPath: "/coterie/add",
        curCoterie: JSON.stringify(emptyCoterie)
      }
    })
  }else {
    showToast("请先登录")
  }
}

</script>

<style scoped>

</style>