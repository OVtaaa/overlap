<template>
  <van-nav-bar
      :title="props.title"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
  />

</template>

<script setup lang="ts">

import {useRouter} from "vue-router";

const router = useRouter()

interface navBarProps {
  title?: string,
}

//@ts-ignore
const props = withDefaults(defineProps<navBarProps>(),{
  title: "标题",
})


const onClickLeft = () => {
    router.back()
}
</script>

<style scoped>

</style>