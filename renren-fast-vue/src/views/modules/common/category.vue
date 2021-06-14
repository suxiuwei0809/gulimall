<template>
    <el-tree
      :data="menus"
      :props="defaultProps"
      :default-expanded-keys="expandedKey"
      node-key="catId"
      :expand-on-click-node="false"
      @node-click="handleNodeClick"
      show-checkbox>
    <span class="custom-tree-node" slot-scope="{ node, data }">
      <span>{{ node.label }}</span>
    </span>
    </el-tree>
</template>

<script>
  //这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
  //例如：import 《组件名称》 from '《组件路径》';

  export default {
    data() {
      return {
        menus: [],
        expandedKey: [],
        dialogVisible:false,
        defaultProps: {
          children: "children",
          label: "name"
        },
        count: 1
      };
    },
    created() {
      this.loadMenus();
    },
    methods: {
      loadMenus() {
        this.$http({
          url: this.$http.adornUrl("/product/category/list/tree"),
          method: "post"
        }).then(res => {
          this.menus = res.data.page;
        });
      },

      handleNodeClick(data,node,comp) {
        console.log("节点被点击"+data)
        this.$emit("tree-node-click",data,node,comp)
      },
      handleClose(){}
    }
  };
</script>
<style scoped>
</style>
