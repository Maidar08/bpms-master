export interface MenuNode {
  name: string;
  icon: string;
  route: string;
  id: string;
  children?: MenuNode[];
}
export const TRANSFORMER = (node: MenuNode, level: number) => {
  return {
    expandable: !!node.children && node.children.length > 0,
    name: node.name,
    icon: node.icon,
    route: node.route,
    level,
  };
}
