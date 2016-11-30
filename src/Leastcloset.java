import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.lang.model.type.ArrayType;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.print.DocFlavor;
import java.util.*;
import java.util.stream.Collector;

/**
 * Created by coder-z on 16-11-25.
 */
public class Leastcloset {
    public static Set<Character> U=new HashSet<Character>();
    public static Map<String,HashSet<Character>> F=new HashMap<String,HashSet<Character>>();
    public static Set<Character> L=new HashSet<Character>();
    public static Set<Character> R=new HashSet<Character>();
    public static List<Set<Character>> TriNF=new LinkedList<Set<Character>>();
    public static String key=null;


    /**
     * 获得主键
     * @return
     */

    public static Set<Character> getPriKey() {
        Set<Character> result=new HashSet<Character>();
        for(String key:F.keySet()) {
            Iterator it=F.get(key).iterator();
            for(char ch:key.toCharArray()) {
                L.add(ch);
                while(it.hasNext()) {
                    char chr=(char)it.next();
                    R.add(chr);
                }
            }
        }
        for(char element:U) {
            if(R.contains(element))
                continue;
            result.add(element);
        }
        key=ArrayToString(result);
        return result;
    }

    /**
     * 初始化数据:将键值对拆分成字符集合
     * @param src
     */
    //(a-(f=t=a),c-d,d-f)   需要分解
    public static void initData(String src) {
        U.addAll(new HashSet<Character>(Arrays.asList('C','T','H','R','S','G')));
        String[] tuples=src.split(",");
        for(String str:tuples) {
            HashSet<Character> characterSet=new HashSet<>();
            String[] temp=str.split("-");
            for(char ch:temp[1].toCharArray()) {
                characterSet.add(ch);
            }
            F.put(temp[0],characterSet);
        }
    }


    /**
     * 求闭包,直接使用尾递归更新数据
     * 思路：遍历键值对的键集合,检查现有闭包是否涵盖每个键集合的字符串，如果涵盖，则递归更新数据，重新遍历检查，直到所有的键集合被遍历完成
     * 同时，为了不重复访问，用visited记录
     * @param src
     * @param visited   已经访问过的键集合
     * @return
     */
    public static TreeSet<Character> getCloset(String src,Set<String> visited) {
        TreeSet<Character> closet=new TreeSet<Character>();

        for(char ch:src.toCharArray()) {
            closet.add(ch);
        }
        if(visited.size()==F.keySet().size()||closet.size()==U.size())
            return new TreeSet<>(U);
        for(String key:F.keySet()) {
            if(contains(key,src))
                continue;
            Iterator<Character> it=F.get(key).iterator();
                if(contains(key,ArrayToString(closet))&&!visited.contains(key)) {
                    while(it.hasNext()) {
                        Character ch=it.next();
                        closet.add(ch);
                    }
                    visited.add(key);
                    return getCloset(ArrayToString(closet),visited);
                }
        }
        return closet;
    }

    /**
     * 求src是否是trg的子串,即src是否被trg包含(不考虑顺序和连续性)
     * @param src
     * @param trg
     * @return
     */

    public static boolean contains(String src,String trg) {
        int tag=0;
        for(char ch:src.toCharArray()) {
            for(char chr:trg.toCharArray())
                if(chr==ch) {
                    tag++;
                    break;
                }
        }
        return tag==src.length();
    }

    /**
     * 字符型集合和字符串类型转换
     * @param set
     * @return
     */

    public static String ArrayToString(Set<Character> set) {                                                                //原来是treeSet，现在换成接口
        StringBuffer buf=new StringBuffer();
        for(char c:set) {
            buf.append(c);
        }
        return buf.toString();

    }

    public static void leftPan() {
        Set<String> keySet=F.keySet();
        Iterator<String> its=keySet.iterator();
        HashSet<String> needCut=new HashSet<String>();
        while(its.hasNext()) {
            String key=its.next();
            Iterator<Character> it=F.get(key).iterator();
            for(char ch:key.toCharArray()) {
                while(it.hasNext()) {
                    Character chr=it.next();
                    if(contains(key,ArrayToString(getCloset(ch+"",new HashSet<>())))) {
                       needCut.add(key);
                    }
                }
            }
        }
        for(String cut:needCut) {
            F.remove(cut);
        }
    }

    /**
     * 求最小函数依赖集的主函数
     * 思路:遍历键值对中的每一个键，分别求其闭包，如果闭包内含有对应的值,则消除该键值对
     *
     */
    public static void getFmin() {
        TreeSet<Character> closet;
        HashSet<String> needCut=new HashSet<String>();
        for(String key:F.keySet()) {
            HashSet<Character> temp=F.get(key);
            System.out.println(temp);
            Iterator<Character> it=temp.iterator();
            while(it.hasNext()) {
                Character ch=it.next();
                if (ArrayToString(getCloset(key,new HashSet<>())).contains(ch+"")) {
                    it.remove();
                    if(temp.isEmpty())
                        needCut.add(key);
                }

            }
        }
        for(String cut:needCut){
            F.remove(cut);
        }
//        leftPan();
    }


    /**
     *
     * @param set
     * @return 返回该集合是否只包含主键内容
     */
    public static boolean isFomatSet(Set<Character> set) {
        if("".equals(key))
            throw new IllegalArgumentException("no key words!");
        return collectionEquals(CharacterOut(key),set);
    }

    /**
     * 比较两个集合中的元素是否完全相同
     * @param c1
     * @param c2
     * @return
     */
    public static boolean collectionEquals(Collection<Character> c1,Collection<Character> c2) {
        if(c1.isEmpty()||c2.isEmpty()||c1.size()!=c2.size())
            return false;
        Iterator it1=c1.iterator();
        Iterator it2=c2.iterator();
        while(it1.hasNext()&&it2.hasNext()) {
            if(it1.next()!=it2.next())
                return false;
        }
        return true;
    }


    public static Set<Character> CharacterOut(String src) {
        Set<Character> result=new HashSet<Character>();
        for(char ch:src.toCharArray())
            result.add(ch);
        if(F.containsKey(src)) {
            for(Character ch:F.get(src)) {
                result.add(ch);
            }
        }
        return result;
    }

    /**
     * 根据集合中的字符返回含有该集合字符所组成的键的集合
     * @param characters
     * @return 返回键的集合,键的元素是由字符集合中元素组成的
     */
    public static Set<String> getKeySet(Collection<Character> characters) {
        Set<String> keySet = new HashSet<>();
        for(String key:F.keySet()) {
            if(collectionEquals(CharacterOut(key),characters))
                keySet.add(key);
        }
        return keySet;
    }

    /**
     * 函数依赖分解的主要逻辑：
     *  遍历键集合,寻找选中所有由集合carpet中的字符组成的键keySet,然后消除reaminder中keySet中所有对应的值,直到找到主键为止
     *  其中使用递归同样是为了更新数据
     * @param remainder 剩余字符集合
     * @param carpet    目前选中的键的字符集合
     * @param visited   已访问过的键集合
     * @return  尾递归
     */
    public static Set<Character> decompose(Set<Character> remainder,Set<Character> carpet,Set<String> visited) {
        if(!carpet.isEmpty())
            TriNF.add(new HashSet<Character>(carpet));                                                                  //加入分解集合
        if(isFomatSet(carpet))                                                                                          //如果选中的字符集就是主键,则结束
            return carpet;
        else
            carpet.clear();
        Set<Character> remainders=remainder;
        Set<Character> carpets=carpet;
        Set<String> visiteds=visited;
        for(String keyStr:F.keySet()) {
            if(!visited.isEmpty()&&visited.contains(keyStr))                                                            //如果数据该键已经被访问过则跳过
                continue;
            else
                visited.add(keyStr);
            Iterator it=F.get(keyStr).iterator();
            for(char c:keyStr.toCharArray()) {                                                                          //手动装载选中的键
                carpet.add(c);
            }
            while(it.hasNext()) {
                carpet.add((Character)it.next());                                                                       //装载对应的值,形成选中集合
            }
            for(String keyS:getKeySet(carpet)) {
                for(Character ch:F.get(keyS)) {
                    remainder.remove(ch);
                }
            }
            return decompose(remainder,carpet,visited);                                                                  //数据更新
        }
        return null;                                                                                                     //结束整个流程
    }

    public static void main(String...args) {
        Scanner input=new Scanner(System.in);
        String src=input.nextLine();
        initData(src);
        getFmin();
        System.out.println(F);
        TriNF.add(getPriKey());
        System.out.println(TriNF);
        decompose(U, new HashSet<Character>(),new HashSet<String>());
        System.out.println(TriNF);
    }

}
