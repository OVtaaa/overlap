<template>
  <van-sticky>
    <!--  顶部搜索条-->
    <van-search
        v-model="value"
        show-action
        shape="round"
        :placeholder="props.placeholder"
        @search="onSearch"
        maxlength="50"
    >
      <template v-slot:left>
        <van-icon name="manager-o" @click="onClickIcon"/>
      </template>
      <template #action>
        <div @click="onClickButton" style="color: #7a8ca2">搜索</div>
      </template>
    </van-search>
  </van-sticky>
</template>

<script setup lang="ts">
import {useRouter} from "vue-router";
import {ref} from "vue";

const value = ref("")
const router = useRouter()
interface topSearchBarProps {
  placeholder?: string,
  path?: string
}
//@ts-ignore
const props = withDefaults(defineProps<topSearchBarProps>(),{
  placeholder: "",
  path: "/"
})

const onSearch = () => {
  if(value.value !== "") {
    router.push({
      path: props.path,
      query: {
        plan: value.value
      }
    })
  }
}

const onClickButton = () => {
  onSearch()
};

const onClickIcon = () => {
  router.push("/user")
}

</script>

<style scoped>

</style>