package com.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.user.Note;
import com.user.Temp;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 对数据库的操作，分装对后台服务器的操作
 * Created by Qzl on 2016-12-10.
 */

public class DataOperate {

    public static void addData(final Context context, BmobObject object){
        object.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null){
                    Toast.makeText(context, "创建数据成功：" + objectId, Toast.LENGTH_SHORT).show();
                    if (sSuccessAndFalied != null){
                        sSuccessAndFalied.success();
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    if (sSuccessAndFalied != null){
                        sSuccessAndFalied.failed();
                    }
                }
            }
        });
    }
    /**
     * 获取 temp值
     * @param Id
     */
    public static void getTemp(String Id){
        BmobQuery<Temp> bmobQuery = new BmobQuery<Temp>();
        //查询一个
        bmobQuery.getObject(Id, new QueryListener<Temp>() {
            @Override
            public void done(Temp temp, BmobException e) {
                if (e == null){
                    if (sTempQuery != null){
                        sTempQuery.tempquery(temp);
                    }
                }else {
                    System.out.println("查询出错："+e.getErrorCode()+","+e.getMessage());
                }
            }
        });
    }

    /**
     * 查询食品信息
     * @param context
     */
    public static void getFoodInfo(Context context){
        BmobQuery<Note> query = new BmobQuery<Note>();
        query.setLimit(50);//限定条数（默认是10条）
        query.findObjects(new FindListener<Note>() {
            @Override
            public void done(List<Note> list, BmobException e) {
                if (e == null){
                    if (sListFood != null){
                        sListFood.listFood(list);
                    }
                }else {
                    System.out.println("查询食物数据失败："+e.getErrorCode()+","+e.getMessage());
                }
            }
        });
    }

    /**
     * 获取单张图片
     * @param context
     * @param img
     * @param fileName
     * @param groupBy
     * @param url
     */
    public static void getIcon(final Context context, final ImageView img, final String fileName, final String groupBy, final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                BmobFile bf = new BmobFile(fileName,groupBy,url);
                final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"";
                final File saveFile = new File(path,bf.getFilename());
                if (!saveFile.exists()) {
                    bf.download(saveFile, new DownloadFileListener() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                Toast.makeText(context, "下载成功，保存路径：" + s, Toast.LENGTH_SHORT).show();
                                if (sPhotoPath != null){
                                    sPhotoPath.photoPath(img,s);
                                }
                            } else {
                                System.out.println("下载失败：" + e.getErrorCode() + "," + e.getMessage());
                            }
                        }
                        @Override
                        public void onProgress(Integer integer, long l) {

                        }
                    });
                }else {
                    if (sPhotoPath != null){
                        sPhotoPath.photoPath(img,path+"/"+bf.getFilename());
                    }
                }
            }
        }).start();
    }

    /**
     * 修改temp表的数据
     * @param context
     * @param Id
     */
    public static void updateData(final Context context, BmobObject obj, String Id){
        //更新数据
        obj.update(Id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    Toast.makeText(context, "修改数据成功", Toast.LENGTH_SHORT).show();
                    if (sOverLoad != null){
                        sOverLoad.overLoad();
                    }
                }else {
                    Toast.makeText(context, "修改数据失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 删除数据
     * @param context
     * @param objName
     * @param Id
     */
    public static void deleteDate(final Context context, BmobObject objName, String Id){
        objName.setObjectId(Id);
        objName.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    Toast.makeText(context, "删除数据成功", Toast.LENGTH_SHORT).show();
                    if (sOverLoad != null){
                        sOverLoad.overLoad();
                    }
                    if (sSuccessAndFalied != null){
                        sSuccessAndFalied.success();
                    }
                }else {
                    Toast.makeText(context, "修改数据失败", Toast.LENGTH_SHORT).show();
                    if (sSuccessAndFalied != null){
                        sSuccessAndFalied.failed();
                    }
                }
            }
        });
    }

    /**
     * 通过sql原声查询
     * @param tableName
     * @param where
     */
    public static void querySql(String tableName,String where){
        String bql1="select * from "+tableName+" where foodname = ?";
        new BmobQuery<Note>().doSQLQuery(bql1, new SQLQueryListener<Note>() {
            @Override
            public void done(BmobQueryResult<Note> result, BmobException e) {
                if (e == null) {
                    if (sListFood != null){
                        sListFood.listFood(result.getResults());
                    }
                } else {
                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        }, where);
    }

    /******接口回调********************************/
    private static TempQuery sTempQuery;
    public static void setOnTemp(TempQuery tempQuery){
        sTempQuery = tempQuery;
    }

    private static ListFood sListFood;
    public static void setOnListFood(ListFood listF){
        sListFood = listF;
    }
    private static PhotoPath sPhotoPath;
    public static void setOnPhotoPath(PhotoPath photoPath){
        sPhotoPath = photoPath;
    }
    private static OverLoad sOverLoad;
    public static void setOnOverLoad(OverLoad overLoad){
        sOverLoad = overLoad;
    }
    public static SuccessAndFalied sSuccessAndFalied;
    public static void setOnSuccessAndFalied(SuccessAndFalied successAndFalied){
        sSuccessAndFalied = successAndFalied;
    }
    public interface TempQuery{
        void tempquery(Object obj);
    }
    public interface ListFood{
        void listFood(List list);
    }
    public interface PhotoPath{
        void photoPath(ImageView img,String str);
    }
    public interface OverLoad{
        void overLoad();
    }

    public interface SuccessAndFalied{
        void success();
        void failed();
    }
}
