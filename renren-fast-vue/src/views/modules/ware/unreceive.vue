<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item label="状态">
        <el-select style="width:120px;" v-model="dataForm.status" placeholder="请选择状态" clearable>
<!--          <el-option label="新建" :value="0"></el-option>-->
          <el-option label="已分配" :value="1"></el-option>
          <el-option label="已领取" :value="2"></el-option>
          <el-option label="已完成" :value="3"></el-option>
          <el-option label="有异常" :value="4"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="关键字">
        <el-input style="width:120px;" v-model="dataForm.key" placeholder="参数名" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>

      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
      style="width: 100%;"
    >
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
      <el-table-column prop="id" header-align="center" align="center" label="采购单id"></el-table-column>
      <el-table-column prop="assigneeId" header-align="center" align="center" label="采购人id"></el-table-column>
      <el-table-column prop="assigneeName" header-align="center" align="center" label="采购人名"></el-table-column>
      <el-table-column prop="phone" header-align="center" align="center" label="联系方式"></el-table-column>
      <el-table-column prop="priority" header-align="center" align="center" label="优先级"></el-table-column>
      <el-table-column prop="status" header-align="center" align="center" label="状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status == 0">新建</el-tag>
          <el-tag type="info" v-if="scope.row.status == 1">已分配</el-tag>
          <el-tag type="warning" v-if="scope.row.status == 2">已领取</el-tag>
          <el-tag type="success" v-if="scope.row.status == 3">已完成</el-tag>
          <el-tag type="danger" v-if="scope.row.status == 4">有异常</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="wareId" header-align="center" align="center" label="仓库id"></el-table-column>
      <el-table-column prop="amount" header-align="center" align="center" label="总金额(￥)"></el-table-column>
      <el-table-column prop="createTime" header-align="center" align="center" label="创建日期"></el-table-column>
      <el-table-column prop="updateTime" header-align="center" align="center" label="更新日期"></el-table-column>
      <el-table-column fixed="right"  header-align="center" align="center" width="150" label="操作">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            v-if="scope.row.status==1"
            @click="receivedHandle(scope.row.id)"
          >领取采购单</el-button>
          <el-button
            type="text"
            size="small"
            v-if="scope.row.status==2"
            @click="doneHandle(scope.row.id)"
          >完成采购单</el-button>

        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageIndex"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="totalPage"
      layout="total, sizes, prev, pager, next, jumper"
    ></el-pagination>
  <purchaseonedetail v-if="dialogVisible" ref="onedetail"  ></purchaseonedetail>
  </div>
</template>

<script>
import purchaseonedetail from "./purchaseonedetail";
export default {
  components:{purchaseonedetail},
  data() {
    return {
      currentRow: {},
      dataForm: {
        key: "",
        status: "1"
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      purchaseId:"",
      dialogVisible: false,
      userId: "",
      userList: []
    };
  },

  activated() {
    this.getDataList();
  },
  created() {

  },
  methods: {


    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/ware/purchase/list"),
        method: "get",
        params: this.$http.adornParams({
          page: this.pageIndex,
          limit: this.pageSize,
          key: this.dataForm.key,
          status: this.dataForm.status
        })
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.page.list;
          this.totalPage = data.page.totalCount;
        } else {
          this.dataList = [];
          this.totalPage = 0;
        }
        this.dataListLoading = false;
      });
    },
    // 每页数
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val;
    },


    // 领取采购单
    receivedHandle(id) {
      var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id;
        });
      this.$confirm(
        `确定对[id=${ids.join(",")}]进行[${id ? "领取" : "批量领取"}]操作?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        this.$http({
          url: this.$http.adornUrl("/ware/purchase/received"),
          method: "post",
          data: this.$http.adornData(ids, false)
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.getDataList();
              }
            });
          } else {
            this.$message.error(data.msg);
          }
        });
      });
    },
    doneHandle(id){
      this.dialogVisible=true;
      this.$nextTick(() => {
        this.$refs.onedetail.init(id)
      })
    },
  }
};
</script>
