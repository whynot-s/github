package cn.springmvc.service;

import cn.springmvc.dao.CooperationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class Cooperation {

    @Autowired
    private CooperationMapper cooperationMapper;

    private int size;
    private Map<Integer, Map<Integer, Integer>> relation;

    public Cooperation(int size){
        relation = new HashMap<Integer, Map<Integer, Integer>>();
        this.size = size;
    }

    public void calculate(){
        ArrayList<Integer> projects = cooperationMapper.selectProjectId_Filter1();
        for(int p = 0; p < projects.size(); p++){
            int project = projects.indexOf(p);
            ArrayList<Integer> members = cooperationMapper.selectMembersByProjectId(project);
            for(int i = 0; i < members.size(); i++){
                Map<Integer, Integer> memberMap = relation.get(members.indexOf(i));
                if(memberMap == null) {
                    memberMap = new HashMap<Integer, Integer>();
                    relation.put(members.indexOf(i), memberMap);
                }
                for(int j = i + 1; j < members.size(); j++){
                    Integer value = memberMap.get(members.indexOf(j));
                    if(value == null){
                        memberMap.put(members.indexOf(j), 1);
                        relation.put(members.indexOf(i), memberMap);
                        Map<Integer, Integer> tmp = relation.get(members.indexOf(j));
                        if(tmp == null){
                            tmp = new HashMap<Integer, Integer>();
                            tmp.put(members.indexOf(i), 1);
                            relation.put(members.indexOf(j), tmp);
                        }else{
                            tmp.put(members.indexOf(i), 1);
                        }
                    }else{
                        memberMap.put(members.indexOf(j), value + 1);
                        relation.put(members.indexOf(i), memberMap);
                        relation.get(members.indexOf(j)).put(members.indexOf(i), value + 1);
                    }
                }
            }
            if(relation.size() >= size || ((relation.size() >0) && (p == projects.size() - 1))){
                List<Map<String, Integer>> output = new ArrayList<Map<String, Integer>>();
                for(Map.Entry<Integer, Map<Integer, Integer>> entry : relation.entrySet()){
                    int userA = entry.getKey();
                    for(Map.Entry<Integer, Integer> en : entry.getValue().entrySet()){
                        Map<String, Integer> tmp = new HashMap<String, Integer>();
                        tmp.put("userA", userA);
                        tmp.put("userB", en.getKey());
                        tmp.put("cop", en.getValue());
                        output.add(tmp);
                    }
                }
                new Thread(new CooperationDB(output, this.cooperationMapper)).start();
            }
            relation.clear();
        }
    }
}


class CooperationDB implements Runnable{

    private CooperationMapper cooperationMapper;

    private List<Map<String, Integer>> relation;

    public CooperationDB(List<Map<String, Integer>> relation, CooperationMapper cooperationMapper){
        this.relation = relation;
        this.cooperationMapper = cooperationMapper;
    }

    public void run(){
        cooperationMapper.insertCooperationBatch(relation);
    }

}