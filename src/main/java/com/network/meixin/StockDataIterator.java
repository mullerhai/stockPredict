package com.network.meixin;



import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
/**
 * Created by muller on 6/26/17.
 */
public class StockDataIterator implements DataSetIterator {


    private static final int VECTOR_SIZE=6;

    private int batchNum;
    private int exampleLength;
    private List<DailyData> dataList;

    private List<Integer> dataRecord;

    private double []maxNum;

    public StockDataIterator(){
        dataRecord=new ArrayList<>();
    }

    public boolean loadData(String fileName,int batchNum,int exampleLength){
        this.batchNum=batchNum;
        this.exampleLength=exampleLength;
        maxNum=new double[6];
        try{
            readDataFromFile(fileName);
        }catch (Exception e){
            e.printStackTrace();
            return false;

        }
        resetDataRecord();
        return true;

    }
    private void resetDataRecord(){
        dataRecord.clear();
        int total=dataList.size()/exampleLength+1;
        for(int i=0;i<total;i++){
            dataRecord.add(i*exampleLength);
        }
    }

    public List<DailyData> readDataFromFile(String fileName)throws  IOException{
        dataList= new ArrayList<>();
        FileInputStream  fis=new FileInputStream(fileName);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
        String line0 =in.readLine();
        String line=in.readLine();
        for (int i = 0; i < maxNum.length; i++) {
            maxNum[i]=0;
        }
        System.out.println("read data ...");
        while (line!=null){
            String [] strArr=line.split(",");
            if(strArr.length>=7) {
                DailyData data= new DailyData ();
                 double [] nums= new double[6];
                 for( int j=0;j<6;j++){
                     nums[j] =Double.valueOf(strArr[j+2]);
                     if(nums[j]>maxNum[j]){
                         maxNum[j]=nums[j];
                     }
                 }
                  //构造data 对象
                 data.setOpenPrice(Double.valueOf(nums[0]));
                data.setClosePrice(Double.valueOf(nums[1 ]));
                data.setMaxPrice(Double.valueOf(nums[2]));
                data.setMinPrice(Double.valueOf(nums[3]));
                data.setTurnover(Double.valueOf(nums[4]));
                data.setVolume(Double.valueOf(nums[5]));
                dataList.add(data);

            }
            line=in.readLine();

        }
        in.close();
        fis.close();
        System.out.println("反转 list 。。。");
        Collections.reverse(dataList);
        return dataList;

    }

    public double [] getMaxArr(){

        return this.maxNum;
    }

    public void reset(){
        resetDataRecord();
    }
    public boolean hasNext(){
        return dataRecord.size()>0;
    }
    public DataSet next(){
        return  next(batchNum);
    }

    public int batch(){
        return batchNum;
    }
    public int cursor(){
        return totalExamples() - dataRecord.size();
    }
    public int numExamples(){
        return  totalExamples();

    }
    public void  setPreProcessor(DataSetPreProcessor preProcessor){
        throw  new UnsupportedOperationException("not implemented  ");

    }

    public DataSetPreProcessor getPreProcessor() {
        return null;
    }

    public int totalExamples(){
        return (dataList.size())/ exampleLength;

    }

    public int inputColumns(){
        return  dataList.size();

    }
    public  int totalOutcomes(){
        return 1;
    }

    public boolean resetSupported() {
        return false;
    }

    public boolean asyncSupported() {
        return false;
    }

    public  List<String > getLabels(){
        throw new UnsupportedOperationException("Not Implemented   ");
    }



    public void remove(){
        throw new UnsupportedOperationException(   );

    }

    public DataSet next(int num ){

        if (dataRecord.size() <=0) {
            throw new NoSuchElementException();

        }
        int actualBatchSize =Math.min(num,dataRecord.size());
        int actualLength=Math.min(exampleLength,dataList.size()-dataRecord.get(0)-1);

        System.out.println("l==="+"  "+actualLength+"  exampleLength :="+exampleLength+"  batchsize:="+actualBatchSize);
        System.out.println("datalist="+dataList.size()+"  dataRecord  "+dataRecord.get(0));
        INDArray input =Nd4j.create(new int[] {actualBatchSize,VECTOR_SIZE,actualLength},'f');
        INDArray label=Nd4j.create(new int[] {actualBatchSize,1,actualLength},'f');
        if (actualLength <1) {
             label=Nd4j.create(new int[] {actualBatchSize,1,1},'f');
        }

        DailyData nextData=null,curData=null;
        for (int i = 0; i < actualBatchSize; i++) {
            int index= dataRecord.remove(0);
            int endIndex= Math.min(index+exampleLength,dataList.size()-1);
            curData=dataList.get(index);
            for (int j = index  ; j <endIndex ; j++) {
                nextData=dataList.get(j+1);

                int c= endIndex-j-1;
                input.putScalar(new int[]{i,0,c},curData.getOpenPrice()/maxNum[0]);
                input.putScalar(new int [] {i,1,c},curData.getClosePrice()/maxNum[1]);
                input.putScalar(new int []{i,2,c},curData.getMaxPrice()/maxNum[2]);
                input.putScalar(new int []{i,3,c},curData.getMinPrice()/maxNum[3]);
                input.putScalar(new int []{i,4,c},curData.getTurnover()/maxNum[4]);
                input.putScalar(new int []{i,5,c},curData.getTurnover()/maxNum[5]);
                label.putScalar(new int []{i,0,c},nextData.getClosePrice()/maxNum[1]);
                curData=nextData;
            }
            if(dataRecord.size()<=0){
                break;
            }
        }

        return new DataSet(input,label);
    }

}
