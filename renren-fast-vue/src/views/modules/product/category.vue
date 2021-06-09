<!--  -->

<template>
  <div>
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
      <span>
        <el-button v-if="node.level<=2" type="text" size="mini" @click="() => append(node, data)">
          Append
        </el-button>
        <el-button v-if="node.childNodes.length==0" type="text" size="mini" @click="() => remove(node, data)">
          Delete
        </el-button>
      </span>
    </span>
  </el-tree>
    <el-dialog
      title="提示"
      :visible.sync="dialogVisible"
      width="30%"
      :before-close="handleClose">
      <span>这是一段信息</span>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
  </span>
    </el-dialog>
  </div>
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
      append(node, data) {
        console.log(node)
        this.dialogVisible=true;
        // const newChild = {id: '', label: "testtest", children: []};
        // if (!data.children) {
        //   this.$set(data, "children", []);
        // }
        // data.children.push(newChild);
      },

      remove(node, data) {
        console.log(node);
        let ids = [data.catId]
        this.$confirm(`确定操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl("/product/category/delete"),
            method: "post",
            data: this.$http.adornData(ids, false)
          }).then(res => {
            if (res.data.msg == 'success') {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
              })
            }
            this.loadMenus();
              //需要默认展开的菜单需要node—key配合使用 不然不展开
             this.expandedKey = [node.parent.data.catId];

          });
        });
      },
      handleNodeClick() {
      },
      handleClose(){}
    }
  };
</script>
<style scoped>
</style>
