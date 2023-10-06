<template>
  <van-sticky>
    <!--  顶部搜索条-->
    <van-search
        v-model="value"
        show-action
        shape="round"
        placeholder="请输入圈号 / 圈名 / 标签"
        @search="onSearch"
        maxlength="50"
    >
      <template v-slot:left>
        <van-icon name="arrow-left" @click="onBack"/>
      </template>
      <template #action>
        <div @click="onClickButton" style="color: #7a8ca2">搜索</div>
      </template>
    </van-search>
  </van-sticky>

  <coterie-card-list ref="coterieListRef" :path="`/coterie/search/mul?text=`+value"/>

</template>

<script setup lang="ts">
import {ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import CoterieCardList from "../../components/coterie/CoterieCardList.vue";


const route = useRoute();
const value = ref(route.query.plan);
const router = useRouter();

const coterieListRef = ref<any>(); // 添加 ref 属性

const onClickButton = () => {
  coterieListRef.value.refreshing = true
  coterieListRef.value.onRefresh()
};

const onSearch = () => {
  onClickButton();
}



const onBack = () => {
  router.back()
}



</script>

<style scoped>

</style>