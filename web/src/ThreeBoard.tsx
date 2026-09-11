import React,{useEffect,useMemo,useRef,useState} from 'react'
import * as THREE from 'three'
import {RoundedBoxGeometry} from 'three/addons/geometries/RoundedBoxGeometry.js'
import {Icon} from '@iconify/react'

export type ThreeStone={id:number;kind:number;x:number;y:number;layer:number}
export type ThreeWorld={bg:string;icons:readonly string[];colors:readonly string[];table:string;accent:string}

type Props={stones:ThreeStone[];tray:ThreeStone[];theme:ThreeWorld;selectedId:number|null;onPick:(s:ThreeStone)=>void}

const iconName=(name:string)=>`game-icons:${name}`
const tileSize=1.02
const gap=1.13
const boardZ=(y:number)=>3-y
const pos=(s:ThreeStone)=>new THREE.Vector3((s.x-3)*gap,0.58+s.layer*.24,boardZ(s.y)*gap)

export default function ThreeBoard({stones,tray,theme,selectedId,onPick}:Props){
 const mount=useRef<HTMLDivElement>(null),sceneRef=useRef<THREE.Scene|null>(null),cameraRef=useRef<THREE.PerspectiveCamera|null>(null),rendererRef=useRef<THREE.WebGLRenderer|null>(null),meshRef=useRef(new Map<number,THREE.Mesh>()),raf=useRef(0),[points,setPoints]=useState<Record<number,{x:number;y:number;z:number;scale:number}>>({})
 const trayPositions=useMemo(()=>tray.map((_,i)=>new THREE.Vector3((i-3)*1.05,0.5,4.7)),[tray])
 const worldColors=useMemo(()=>theme.colors.map(v=>new THREE.Color(v)),[theme.colors])

 useEffect(()=>{
  const el=mount.current;if(!el)return
  const scene=new THREE.Scene();scene.background=new THREE.Color(theme.table)
  const camera=new THREE.PerspectiveCamera(42,1,.1,100);camera.position.set(0,9.3,11.8);camera.lookAt(0,.25,0)
  const renderer=new THREE.WebGLRenderer({antialias:true,alpha:false,powerPreference:'high-performance'});renderer.setPixelRatio(Math.min(window.devicePixelRatio,1.8));renderer.shadowMap.enabled=true;renderer.shadowMap.type=THREE.PCFSoftShadowMap;renderer.outputColorSpace=THREE.SRGBColorSpace;renderer.setClearColor(theme.table,1);el.appendChild(renderer.domElement)
  const hemi=new THREE.HemisphereLight(0xffffff,0x263044,2.1);scene.add(hemi)
  const key=new THREE.DirectionalLight(0xffffff,3.5);key.position.set(-4,10,7);key.castShadow=true;key.shadow.mapSize.set(1024,1024);key.shadow.camera.left=-10;key.shadow.camera.right=10;key.shadow.camera.top=10;key.shadow.camera.bottom=-10;scene.add(key)
  const rim=new THREE.PointLight(new THREE.Color(theme.accent),5,18);rim.position.set(5,4,-5);scene.add(rim)
  const floor=new THREE.Mesh(new RoundedBoxGeometry(11,0.7,11,5,.35),new THREE.MeshStandardMaterial({color:new THREE.Color(theme.table),roughness:.72,metalness:.08}));floor.position.y=-.45;floor.receiveShadow=true;scene.add(floor)
  const inlay=new THREE.Mesh(new RoundedBoxGeometry(9.4,.16,8.2,5,.18),new THREE.MeshStandardMaterial({color:0x171b29,roughness:.45,metalness:.18}));inlay.position.y=-.02;inlay.receiveShadow=true;scene.add(inlay)
  const frame=new THREE.Mesh(new RoundedBoxGeometry(9.7,.28,8.5,5,.22),new THREE.MeshStandardMaterial({color:new THREE.Color(theme.accent),roughness:.3,metalness:.5,transparent:true,opacity:.55}));frame.position.y=.05;scene.add(frame)
  const trayBase=new THREE.Mesh(new RoundedBoxGeometry(8.5,.38,1.65,5,.22),new THREE.MeshStandardMaterial({color:0x111522,roughness:.38,metalness:.28}));trayBase.position.set(0,.16,4.7);trayBase.receiveShadow=true;trayBase.castShadow=true;scene.add(trayBase)
  for(let i=0;i<7;i++){const slot=new THREE.Mesh(new RoundedBoxGeometry(.9,.12,1.15,4,.12),new THREE.MeshStandardMaterial({color:0x252b3c,roughness:.55,metalness:.15}));slot.position.set((i-3)*1.05,.38,4.7);scene.add(slot)}
  sceneRef.current=scene;cameraRef.current=camera;rendererRef.current=renderer
  const resize=()=>{const w=el.clientWidth||320,h=el.clientHeight||560;camera.aspect=w/h;camera.updateProjectionMatrix();renderer.setSize(w,h,false)};resize();const ro=new ResizeObserver(resize);ro.observe(el)
  const tick=()=>{raf.current=requestAnimationFrame(tick);const now=performance.now()/1000;meshRef.current.forEach((m)=>{const selected=m.userData.selected as boolean;if(selected){m.position.y+=((1.65-m.position.y)*.12);m.rotation.y+=.035;m.rotation.z=Math.sin(now*8)*.04}else{m.rotation.y+=(Math.sin(now+m.userData.phase)*.0015-m.rotation.y)*.08}});renderer.render(scene,camera);const next:Record<number,{x:number;y:number;z:number;scale:number}>={};meshRef.current.forEach((m,id)=>{const p=m.position.clone().add(new THREE.Vector3(0,.62,0));p.project(camera);const r=renderer.domElement.getBoundingClientRect();next[id]={x:(p.x*.5+.5)*r.width,y:(-p.y*.5+.5)*r.height,z:p.z,scale:Math.max(.7,Math.min(1.3,12/(m.position.distanceTo(camera.position)+.01)))} });setPoints(next)};tick()
  return()=>{cancelAnimationFrame(raf.current);ro.disconnect();renderer.dispose();el.removeChild(renderer.domElement);scene.clear();meshRef.current.clear()}
 },[theme.table,theme.accent])

 useEffect(()=>{const scene=sceneRef.current;if(!scene)return;const old=[...meshRef.current.values()];old.forEach(m=>{scene.remove(m);m.geometry.dispose();(m.material as THREE.Material).dispose()});meshRef.current.clear();stones.forEach((s,i)=>{const geo=new RoundedBoxGeometry(tileSize,.42,tileSize,5,.13);const mat=new THREE.MeshStandardMaterial({color:worldColors[s.kind%worldColors.length],roughness:.32,metalness:.12});const m=new THREE.Mesh(geo,mat);const p=pos(s);m.position.copy(p);m.castShadow=true;m.receiveShadow=true;m.userData={id:s.id,selected:s.id===selectedId,phase:i*.37};m.rotation.y=(s.id%7)*.06;scene.add(m);meshRef.current.set(s.id,m)})},[stones,selectedId,worldColors])

 useEffect(()=>{meshRef.current.forEach((m,id)=>{m.userData.selected=id===selectedId})},[selectedId])
 const overlays=stones.map(s=>{const p=points[s.id];if(!p)return null;const blocked=stones.some(o=>o.id!==s.id&&o.layer>s.layer&&o.x===s.x&&o.y===s.y);return <button key={s.id} className={`three-icon-button ${blocked?'blocked':''} ${selectedId===s.id?'picked':''}`} style={{left:p.x,top:p.y,transform:`translate(-50%,-50%) scale(${p.scale})`,zIndex:Math.round((1-p.z)*1000)}} disabled={blocked||selectedId!==null} onClick={()=>onPick(s)}><Icon icon={iconName(theme.icons[s.kind%theme.icons.length])}/></button>})
 const trayIcons=tray.map((s,i)=><div key={s.id} className="three-tray-icon" style={{left:`calc(50% + ${(i-3)*1.05/8.5*100}%)`}}><Icon icon={iconName(theme.icons[s.kind%theme.icons.length])}/></div>)
 return <div className={`three-board world-${theme.bg}`}><div ref={mount} className="three-canvas"/><div className="three-overlays">{overlays}</div><div className="three-tray-overlays">{trayIcons}</div><div className="three-depth-glow"/></div>
}
