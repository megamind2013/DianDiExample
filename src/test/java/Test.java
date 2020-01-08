import java.util.Arrays;

public class Test {
    public static void main(String[] args){
//        for(int i=0;i<12;i++){
//            System.out.println(bitwiseComplement(i));
//        }
//        System.out.println(numPairsDivisibleBy60(new int[]{30,20,150,100,40}));
//        System.out.println(numPairsDivisibleBy60(new int[]{60,60,60,60}));

//        System.out.println(shipWithinDays(new int[]{1,2,3,4,5,6,7,8,9,10},5));
//
//        System.out.println(shipWithinDays(new int[]{3,2,2,4,1,4},3));
//
//        System.out.println(shipWithinDays(new int[]{1,2,3,1,1},4));

        System.out.println(numDupDigitsAtMostN(765));
    }
    // 十进制整数的反码
    public static int bitwiseComplement(int N) {
        int B = 2;
        while(B <= N) B *= 2;
        return B - N - 1;
    }
    // 总持续时间可被 60 整除的歌曲
    static int  numPairsDivisibleBy60(int[] time) {
        int[] array = new int[60];
        int result=0;

        for(int element : time)
        {
            result+=array[(60-element%60)%60];

            array[element%60]++;
            System.out.println(element+"------"+result+"----------"+Arrays.toString(array));
        }
        return result;
    }
    // 在D天内送达包裹的能力
    static int shipWithinDays(int[] weights, int D) {
        int maxWeight=0,load=0,mid,j=0,k=0;
        for(int element : weights)
        {
            maxWeight=Math.max(maxWeight,element);
            load+=element;
        }

        while(maxWeight<load)
        {
            mid=(maxWeight+load)/2;

            for(int element : weights)
            {
                if(j+element>mid)
                {
                    j=0;
                    k++;
                }
                j+=element;
            }
            j=0;
            k++;
            if(k<=D)
                load=mid;
            else
                maxWeight=mid+1;
            // clear
            j=k=0;
        }

        return load;
    }

    //
//    int power(int k)
//    {
//        int ans=1;
//        for(int i=1;i<=k;++i)
//            ans*=10;
//        return ans;
//    }

    static int numDupDigitsAtMostN(int N) {
       int result = 0;
       int j=0;
       int[] array = new int[10];

       for(int i=0;i<=N;i++){

//           char[] charArr = Integer.toString(i).toCharArray();
//           for(char c : charArr){
//               array[c-48]++;
//           }

           j = i%10;
           i=i/10;
           while(i>0){
               array[j]++;
               j = i%10;
               i=i/10;
           }

           for(int element : array){
               if(element > 1){
                   result++;
                   break;
               }
           }
           Arrays.fill(array,0);
       }

       return result;

    }
}
